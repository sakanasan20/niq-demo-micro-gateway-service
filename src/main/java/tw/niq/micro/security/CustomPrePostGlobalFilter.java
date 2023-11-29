package tw.niq.micro.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import reactor.core.publisher.Mono;

@Configuration
public class CustomPrePostGlobalFilter {
	
	private final Logger logger = LoggerFactory.getLogger(CustomPrePostGlobalFilter.class);

	@Order(1)
	@Bean
	public GlobalFilter globalFilter1() {
		return (exchange, chain) -> {
			logger.info("## GlobalFilter-1: Pre-Processing...");
			return chain.filter(exchange).then(Mono.fromRunnable(() -> {
				logger.info("## GlobalFilter-1: Post-Processing...");
			}));
		};
	}
	
	@Order(2)
	@Bean
	public GlobalFilter globalFilter2() {
		return (exchange, chain) -> {
			logger.info("## GlobalFilter-2: Pre-Processing...");
			return chain.filter(exchange).then(Mono.fromRunnable(() -> {
				logger.info("## GlobalFilter-2: Post-Processing...");
			}));
		};
	}
	
	@Order(3)
	@Bean
	public GlobalFilter globalFilter3() {
		return (exchange, chain) -> {
			logger.info("## GlobalFilter-3: Pre-Processing...");
			return chain.filter(exchange).then(Mono.fromRunnable(() -> {
				logger.info("## GlobalFilter-3: Post-Processing...");
			}));
		};
	}
	
}
