package ru.netology.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.netology.model.User;
import ru.netology.repository.UserRepository;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserRepository dataUserRepository;

    @GetMapping("/users")
    public List<User> getUsers() {
        return dataUserRepository.findAll();
    }
}
