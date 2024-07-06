package com.poc.demosof.apigateway;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.security.web.server.csrf.ServerCsrfTokenRequestAttributeHandler;
import org.springframework.security.web.server.csrf.XorServerCsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@SpringBootApplication
@EnableWebFluxSecurity
public class ApiGatewayApplication {


    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @Autowired
    ReactiveClientRegistrationRepository clientRegistrationRepository;

    private ServerLogoutSuccessHandler serverLogoutSuccessHandler() {
        OidcClientInitiatedServerLogoutSuccessHandler successHandler = new OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository);
        //needs to match the one declared at auth-server
        successHandler.setPostLogoutRedirectUri("{baseUrl}/api-docs");
        return successHandler;
    }

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        http.cors(withDefaults())
                .csrf(csrfSpec -> csrfSpec.csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new ApiGatewayApplication.SpaServerCsrfTokenRequestHandler()))
                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec
                        .pathMatchers("/notification/ws-connect").hasAuthority("SCOPE_notification")
                        .anyExchange().authenticated())
                .oauth2Login(withDefaults())
                .oauth2ResourceServer(oAuth2ResourceServerSpec -> oAuth2ResourceServerSpec.jwt(withDefaults()))
                .logout(httpSecurityLogoutConfigurer -> httpSecurityLogoutConfigurer.logoutSuccessHandler(serverLogoutSuccessHandler()));
        return http.build();
    }


    static final class SpaServerCsrfTokenRequestHandler extends ServerCsrfTokenRequestAttributeHandler {
        private final ServerCsrfTokenRequestAttributeHandler delegate = new XorServerCsrfTokenRequestAttributeHandler();

        @Override
        public void handle(ServerWebExchange exchange, Mono<CsrfToken> csrfToken) {
            this.delegate.handle(exchange, csrfToken);
        }

        @Override
        public Mono<String> resolveCsrfTokenValue(ServerWebExchange exchange, CsrfToken csrfToken) {
            final var hasHeader = exchange.getRequest().getHeaders().get(csrfToken.getHeaderName()) != null;
            return hasHeader ? super.resolveCsrfTokenValue(exchange, csrfToken) : this.delegate.resolveCsrfTokenValue(exchange, csrfToken);
        }
    }

    @Bean
    //Needed in order to set the XSRF-TOKEN cookie
    //See https://docs.spring.io/spring-security/reference/reactive/integrations/cors.html
    public WebFilter csrfCookieWebFilter() {
        return (exchange, chain) -> {
            exchange.getAttributeOrDefault(CsrfToken.class.getName(), Mono.empty()).subscribe(o -> ((CsrfToken) o).getToken());
            return chain.filter(exchange);
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:63342"));
        configuration.setAllowedMethods(List.of(CorsConfiguration.ALL));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(List.of(CorsConfiguration.ALL));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


}
