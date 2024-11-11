package ru.netology.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.netology.service.PasswordEncryptionService;

@Component
public class EncryptPasswordsRunner implements CommandLineRunner {

    private final PasswordEncryptionService passwordEncryptionService;

    @Autowired
    public EncryptPasswordsRunner(PasswordEncryptionService passwordEncryptionService) {
        this.passwordEncryptionService = passwordEncryptionService;
    }

    @Override
    public void run(String... args) {
        passwordEncryptionService.encryptPasswords();
        System.out.println("Пароли зашифрованы.");
    }
}

