package com.example.Articles.service;

import com.example.Articles.entity.User;
import com.example.Articles.repository.UserRepository;
import com.example.Articles.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    public Optional<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public User createUser(User user) {
        System.out.println(" Сохранение пользователя в БД: " + user);
        User savedUser = userRepository.save(user);
        System.out.println(" Пользователь сохранен: " + savedUser);
        return savedUser;
    }


    @Override
    public User updateUser(UUID id, User updatedUser) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setUsername(updatedUser.getUsername());
                    user.setEmail(updatedUser.getEmail());
                    user.setBio(updatedUser.getBio());
                    user.setImage_url(updatedUser.getImage_url());
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
    }

    @Override
    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }
}
