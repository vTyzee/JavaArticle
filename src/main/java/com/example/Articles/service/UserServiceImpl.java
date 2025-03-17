package com.example.Articles.service;

import com.example.Articles.entity.User;
import com.example.Articles.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        // При использовании @GeneratedValue(strategy = GenerationType.IDENTITY)
        // не нужно вручную генерировать ID. Оно сгенерируется в БД.
        System.out.println("📥 Сохранение пользователя в БД: " + user);
        User savedUser = userRepository.save(user);
        System.out.println("✅ Пользователь сохранен: " + savedUser);
        return savedUser;
    }

    @Override
    public User updateUser(Long id, User updatedUser) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setUsername(updatedUser.getUsername());
                    user.setEmail(updatedUser.getEmail());
                    user.setBio(updatedUser.getBio());
                    user.setImage_url(updatedUser.getImage_url());
                    // Если нужно менять пароль, добавьте:
                    // user.setPassword(updatedUser.getPassword());
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
