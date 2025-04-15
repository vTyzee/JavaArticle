package com.example.articles.service;

import com.example.articles.entities.User;
import com.example.articles.repositories.ArticleCommentRepository;
import com.example.articles.repositories.ArticleRepository;
import com.example.articles.repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;

    public UserServiceImpl(UserRepository userRepository,
                           ArticleRepository articleRepository,
                           ArticleCommentRepository articleCommentRepository) {
        this.userRepository = userRepository;
        this.articleRepository = articleRepository;
        this.articleCommentRepository = articleCommentRepository;
    }

    private BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    public void saveUser(User user) {
        user.setPassword(encoder().encode(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User createUser(User user) {
        user.setPassword(encoder().encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, User updatedUser) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setUsername(updatedUser.getUsername());
                    user.setEmail(updatedUser.getEmail());
                    user.setBio(updatedUser.getBio());
                    user.setImageUrl(updatedUser.getImageUrl());

                    if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                        String encodedPassword = encoder.encode(updatedUser.getPassword());
                        user.setPassword(encodedPassword);
                    }

                    return userRepository.save(user);
                })
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        articleCommentRepository.deleteByUserId(id);
        articleRepository.deleteByOwnerId(id);
        userRepository.deleteById(id);
    }
}
