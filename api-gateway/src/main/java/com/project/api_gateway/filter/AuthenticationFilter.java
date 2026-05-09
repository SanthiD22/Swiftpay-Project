package com.project.api_gateway.filter;

import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private RouteValidator validator;

    // Use WebClient instead of RestTemplate for Reactive Gateway
    @Autowired
    private WebClient.Builder webClientBuilder;

    public AuthenticationFilter() {
        super(Config.class);
    }

    public static class Config {}

    @Override
    public GatewayFilter apply(Config config){
        return ((exchange, chain) -> {
            // 1. Check if the URL is secured (e.g., /wallet/pay)
            if(validator.isSecured.test(exchange.getRequest())) {

                // 2. Error check: Is the Header missing?
                if(!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    throw new RuntimeException("Missing Authorization Header");
                }

                // 3. Extract the Token string
                String authHeader = exchange.getRequest().getHeaders().get(org.springframework.http.HttpHeaders.AUTHORIZATION).get(0);
                if(authHeader != null && authHeader.startsWith("Bearer ")){
                    authHeader = authHeader.substring(7);
                }

                return webClientBuilder.build()
                        .get()
                        .uri("http://IDENTITY-SERVICE/auth/validate?token=" +authHeader)
                        .retrieve()
                        // NEW: Handle specific error status codes
                        .onStatus(HttpStatusCode::isError, clientResponse ->
                                Mono.error(new RuntimeException("Unauthorized access to Swiftpay")))
                        .bodyToMono(String.class)
                        .flatMap(response -> chain.filter(exchange))
                        .onErrorResume((error -> {
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            return exchange.getResponse().setComplete();
                        }));

            }
            return chain.filter(exchange);

        });
    }

}
