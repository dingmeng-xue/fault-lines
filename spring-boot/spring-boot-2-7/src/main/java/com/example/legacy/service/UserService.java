package com.example.legacy.service;

import com.example.legacy.dto.UserDto;
import com.example.legacy.model.User;
import com.example.legacy.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User createUser(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        return userRepository.save(user);
    }

    public Optional<User> updateUser(Long id, UserDto userDto) {
        return userRepository.findById(id).map(user -> {
            user.setUsername(userDto.getUsername());
            user.setEmail(userDto.getEmail());
            if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            }
            return userRepository.save(user);
        });
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

}
