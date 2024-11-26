package ru.netology.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.BadCredentialsException;
import ru.netology.model.User;
import ru.netology.repository.UserRepository;
import ru.netology.security.JwtTokenProvider;

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
    public String authenticate(String email, String password) {
        User user = userRepository.findByLogin(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
        if (passwordEncoder.matches(password, user.getPassword())) {
            return jwtTokenProvider.createToken(user.getLogin());
        } else {
            throw new BadCredentialsException("Неправильный пароль");
        }
    }

    public void logout(String authToken) {
        if (authToken == null || !jwtTokenProvider.validateToken(authToken)) {
            throw new IllegalArgumentException("Invalid or expired token");
        }
        jwtTokenProvider.invalidateToken(authToken);
    }
}