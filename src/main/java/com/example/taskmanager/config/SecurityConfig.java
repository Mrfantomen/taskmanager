package com.example.taskmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    http.authorizeHttpRequests(auth -> auth
	            .requestMatchers(
	                    "/auth/**",
	                    "/swagger-ui/**",
	                    "/swagger-ui.html",
	                    "/v3/api-docs/**"
	            ).permitAll()
	            .anyRequest().authenticated())
	        .httpBasic(basic -> {})
	        .csrf(csrf -> csrf.disable());

	    return http.build();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
	    // Argon2id with OWASP-recommended parameters (as of 2024):
	    // saltLength=16 bytes, hashLength=32 bytes, parallelism=1,
	    // memory=65536 KB (64 MB), iterations=3.
	    // Source: https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html
	    return new Argon2PasswordEncoder(16, 32, 1, 65536, 3);
	}

}
