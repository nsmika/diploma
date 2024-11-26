package ru.netology.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import ru.netology.model.Login;
import ru.netology.service.AuthService;
import ru.netology.model.User;
import ru.netology.model.Error;
import ru.netology.security.JwtTokenProvider;
import ru.netology.service.LoginRequest;

import java.util.HashMap;
import java.util.Map;

@RestController
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
            // Аутентификация и генерация токена
            String token = authService.authenticate(user.getLogin(), user.getPassword());

            // Создаем объект Login для успешного ответа
            Login loginResponse = new Login();
            loginResponse.setAuthToken(token);

            response.put("status", HttpStatus.OK.value());  // Статус 200 (OK)
            response.put("auth-token", loginResponse.getAuthToken()); // Токен

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (BadCredentialsException e) {
            // Обработка ошибок аутентификации
            Error errorResponse = new Error();
            errorResponse.setError("Bad credentials");

            response.put("status", HttpStatus.BAD_REQUEST.value());  // Статус 400 (Bad Request)
            response.put("error", errorResponse.getError()); // Ошибка

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            // Обработка других ошибок
            Error errorResponse = new Error();
            errorResponse.setError("Authorization error");

            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());  // Статус 500 (Internal Server Error)
            response.put("error", errorResponse.getError()); // Ошибка

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