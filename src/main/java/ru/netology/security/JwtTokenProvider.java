package ru.netology.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    // Метод для создания токена
    public String createToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))  // срок действия (1 час)
                .signWith(secretKey)
                .compact();
    }

    // Метод для извлечения email пользователя из токена
    public String getUserEmailFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Метод для проверки валидности токена
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Метод для деактивации токена (например, добавление в черный список)
    public void invalidateToken(String token) {
        // Логика для деактивации токена (например, добавление в черный список или база данных)
        // В этом примере просто удалим его, если нужно, добавьте сюда обработку с удалением
        System.out.println("Токен деактивирован: " + token);
    }
}
