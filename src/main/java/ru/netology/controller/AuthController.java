package ru.netology.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import ru.netology.service.AuthService;
import ru.netology.model.User;
import ru.netology.security.JwtTokenProvider;

import java.util.HashMap;
import java.util.Map;

@RestController
//@CrossOrigin(origins = "http://localhost:8081")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Попытка аутентификации
            String token = authService.authenticate(user.getEmail(), user.getPassword());
            response.put("auth-token", token);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            // Если ошибка валидации
            response.put("email", "Некорректный email");
            response.put("password", "Неверный пароль");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            // Для других ошибок
            response.put("error", "Ошибка авторизации");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestHeader("auth-token") String authToken) {
        authService.logout(authToken);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Выход выполнен");
        return ResponseEntity.ok(response);
    }
}