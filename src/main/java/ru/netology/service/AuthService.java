package ru.netology.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.netology.model.User;
import ru.netology.repository.UserRepository;
import ru.netology.security.JwtTokenProvider;
import org.springframework.security.authentication.BadCredentialsException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // Метод для аутентификации
    public String authenticate(String login, String password) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        if (passwordEncoder.matches(password, user.getPassword())) {
            return jwtTokenProvider.createToken(user.getLogin());  // Генерация токена
        } else {
            throw new BadCredentialsException("Неправильный пароль");
        }
    }

    // Метод для выхода (деактивация токена)
    public void logout(String authToken) {

        if (authToken == null || !jwtTokenProvider.validateToken(authToken)) {
            throw new IllegalArgumentException("Неверный или истекший токен");
        }

        jwtTokenProvider.invalidateToken(authToken);
    }
}