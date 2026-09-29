package ru.wms.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.wms.model.User;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private final long jwtExpiration;
    private final SecretKey signingKey;

    // Внедряем настройки из application.yaml через конструктор
    public JwtService(
            @Value("${wms.jwt.secret}") String secretKey,
            @Value("${wms.jwt.expiration}") long jwtExpiration
    ) {
        this.jwtExpiration = jwtExpiration;
        this.signingKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Генерация JWT-токена для авторизованного сотрудника склада.
     */
    public String generateToken(User user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", user.getRole().name());
        extraClaims.put("companyId", user.getCompany().getId());
        extraClaims.put("companyName", user.getCompany().getName());
        extraClaims.put("name", user.getName());

        return Jwts.builder()
            .claims(extraClaims)
            .subject(user.getEmail())
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
            .signWith(signingKey)
            .compact();
    }

    /**
     * Извлечение Email (username) из токена.
     */
    @SuppressWarnings("null")
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Проверка валидности токена (соответствует ли email и не истек ли срок действия).
     */
    public boolean isTokenValid(String token, String userEmail) {
        final String email = extractEmail(token);
        return (email.equals(userEmail) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    @SuppressWarnings("null")
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Универсальный метод для извлечения конкретного свойства (Claim) из токена.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Парсинг токена и безопасное извлечение всех Claims с проверкой цифровой подписи.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(signingKey) // Проверяем подпись токена перед расшифровкой
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}
