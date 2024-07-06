package com.poc.demosof.notificationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.messaging.web.csrf.CsrfChannelInterceptor;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import static org.springframework.security.config.Customizer.withDefaults;

@SpringBootApplication
@EnableWebSecurity
@EnableWebSocketSecurity
public class NotificationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationServiceApplication.class, args);
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
				.csrf(csrfSpec -> csrfSpec.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
				.oauth2ResourceServer((oauth2) -> oauth2.jwt(withDefaults()));
		return http.build();
	}

	//this fixes csrf header - cookie match
	//https://stackoverflow.com/questions/78666526/how-to-secure-microservice-websocket-endpoint-with-spring-cloud-gateway?noredirect=1#comment138726439_78666526
	@Bean
	public ChannelInterceptor csrfChannelInterceptor() {
		return new CsrfChannelInterceptor();
	}


}
