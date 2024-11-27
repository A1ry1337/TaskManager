package com.taskmanager.user_service.config;

import com.taskmanager.user_service.service.JwtTokenService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class TestSecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/auth/login", "/auth/register", "/users", "/users/*").permitAll()  // Публичные маршруты для логина и регистрации
                        .anyRequest().authenticated() // Все остальные запросы требуют аутентификации
                );
        return http.build();
    }
}
