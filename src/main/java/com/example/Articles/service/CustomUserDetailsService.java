package com.example.Articles.service;

import com.example.Articles.entity.User;
import com.example.Articles.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

/**
 * Сервис, который даёт Spring Security данные о пользователе при логине.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    // Логин по email
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Ищем пользователя в БД по email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found by email: " + email)
                );

        // Возвращаем Spring Security объект UserDetails
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())          // username = email
                .password(user.getPassword())       // пароль (должен быть захеширован)
                .roles(user.getRole().name())       // "ADMIN" или "USER"
                .build();
    }
}
