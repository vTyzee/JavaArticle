package com.example.Articles.repository;

import com.example.Articles.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAll();

    // Поиск по username (если нужно)
    Optional<User> findByUsername(String username);

    // Поиск по email (для логина)
    Optional<User> findByEmail(String email);
}
