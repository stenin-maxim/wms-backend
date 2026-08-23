package ru.wmsparts.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            // Настраиваем правила доступа к URL
            .authorizeHttpRequests(auth -> auth
                // Разрешаем всем заходить на эндпоинты входа и регистрации компаний
                .requestMatchers("/api/v1/auth/**").permitAll()
                // Все остальные запросы к складу требуют обязательной авторизации
                .anyRequest().authenticated()
            )
            
            // Отключаем хранение сессий на сервере (Stateless)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        return http.build();
    }

    // Создаем компонент хэширования BCrypt, который мы будем использовать при регистрации и входе
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}