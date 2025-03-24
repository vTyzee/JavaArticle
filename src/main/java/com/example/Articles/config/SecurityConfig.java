package com.example.Articles.config;

import com.example.Articles.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(
            CustomUserDetailsService customUserDetailsService,
            BCryptPasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(customUserDetailsService);
        auth.setPasswordEncoder(passwordEncoder);
        return auth;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           DaoAuthenticationProvider authProvider) throws Exception {

        // Подключаем провайдер
        http.authenticationProvider(authProvider);

        // Основные правила доступа
        http.authorizeHttpRequests(auth -> auth
                // Страница /login (и, например, /register) должна быть доступна без входа
                .requestMatchers("/login").permitAll()
                // Если нужно показать статьи без входа
                .requestMatchers("/", "/articles", "/articles/{id:\\d+}").permitAll()

                // Допустим, на /users/new и /users — только ADMIN
                .requestMatchers("/users/new", "/users").hasRole("ADMIN")

                // Прочие...
                .anyRequest().authenticated()
        );

        // Настраиваем форму логина
        http.formLogin(login -> login
                .loginPage("/login")          // кастомная страница логина
                .usernameParameter("email")    // логинимся по email
                // Если всё успешно — редиректим на /users и принудительно (true)
                .defaultSuccessUrl("/users", true)
                .permitAll()
        );

        // Логаут
        http.logout(logout -> logout
                .logoutSuccessUrl("/")
                .permitAll()
        );

        return http.build();
    }
}
