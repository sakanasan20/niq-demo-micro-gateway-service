package tw.niq.micro.security;

import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import reactor.core.publisher.Mono;

@Component
public class CustomAuthorizationHeaderFilter extends AbstractGatewayFilterFactory<CustomAuthorizationHeaderFilter.Config> {

	private final Environment environment;
	
	public CustomAuthorizationHeaderFilter(Environment environment) {
		super(Config.class);
		this.environment = environment;
	}

	public static class Config {
		
	}
	
	@Override
	public GatewayFilter apply(Config config) {
		return (exchange, chain) -> {
			
			ServerHttpRequest serverHttpRequest = exchange.getRequest();
			
			if (!serverHttpRequest.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
				return onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED);
			}
			
			String authorizationHeader  = serverHttpRequest.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
			String jwt = authorizationHeader.replace("Bearer ", "");
			
			if (!isJwtValid(jwt)) {
				return onError(exchange, "JWT is not valid", HttpStatus.UNAUTHORIZED);
			}
			
			return chain.filter(exchange);
		};
	}

	private boolean isJwtValid(String jwt) {
		
		String tokenSecret = environment.getProperty("tw.niq.token.secret");
		byte[] tokenSecretBytes = Base64.getEncoder().encode(tokenSecret.getBytes());
		SecretKey secretKey = new SecretKeySpec(tokenSecretBytes, SignatureAlgorithm.HS512.getJcaName());
		
		JwtParser jwtParser = Jwts.parserBuilder()
				.setSigningKey(secretKey)
				.build();
		
		String subject;
		
		try {
			Jwt<Header, Claims> parsedJwt = jwtParser.parse(jwt);
			subject = parsedJwt.getBody().getSubject();
			
			if (subject == null || subject.isEmpty()) {
				return false;
			}
			
			return true;
			
		} catch (Exception e) {
			return false;
		}
	}

	private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
		ServerHttpResponse serverHttpResponse = exchange.getResponse();
		serverHttpResponse.setStatusCode(httpStatus);
		return serverHttpResponse.setComplete();
	}

}
