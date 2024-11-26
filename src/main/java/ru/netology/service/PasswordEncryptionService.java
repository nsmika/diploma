package ru.netology.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.netology.model.User;
import ru.netology.repository.UserRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class PasswordEncryptionService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PasswordEncryptionService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void encryptPasswords() {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            String rawPassword = user.getPassword();

            if (!rawPassword.startsWith("$2a$")) {
                String encodedPassword = passwordEncoder.encode(rawPassword);
                user.setPassword(encodedPassword);
            }
        }

        userRepository.saveAll(users);
    }

    public void encryptPasswordsAsync() {
        CompletableFuture.runAsync(this::encryptPasswords);
    }
}