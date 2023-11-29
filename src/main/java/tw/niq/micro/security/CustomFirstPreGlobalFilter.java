package tw.niq.micro.security;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class CustomFirstPreGlobalFilter implements GlobalFilter, Ordered {
	
	private final Logger logger = LoggerFactory.getLogger(CustomFirstPreGlobalFilter.class);

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		logger.info("## GlobalFilter-0: Pre-Processing...");
		
		String requestPath = exchange.getRequest().getPath().toString();
		logger.info("# Request-Path: " + requestPath);
		
		HttpHeaders httpHeaders = exchange.getRequest().getHeaders();
		Set<String> headerNames = httpHeaders.keySet();
		headerNames.forEach((headerName) -> {
			String headerValue = httpHeaders.getFirst(headerName);
			logger.info("# " + headerName + ": " + headerValue);
		});
		
		return chain.filter(exchange);
	}

	@Override
	public int getOrder() {
		return 0;
	}

}
