package be.security;

import be.entity.Permission;
import be.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {

    private static final String JWT_SECRET = "my-secret-key-my-secret-key-my-secret-key";
    private static final long JWT_EXPIRATION = 1000 * 60 * 60 * 24; // 1 day
    private static final long RESET_TOKEN_EXPIRATION = 1000 * 60 * 15; // 15 phút

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(JWT_SECRET.getBytes());
    }

    public String generateToken(User user) {

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);

        List<String> permissions = user.getRole().getPermissions()
                .stream()
                .map(Permission::getCode)
                .toList();

        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("role", user.getRole().getCode())
                .claim("permissions", permissions)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateResetToken(String email) {

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + RESET_TOKEN_EXPIRATION);

        return Jwts.builder()
                .setSubject(email) // 🔥 dùng email thay vì username
                .claim("type", "RESET_PASSWORD") // 🔥 phân biệt loại token
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getEmailFromToken(String token) {
        return getClaims(token).getSubject();
    }

    public void validateResetToken(String token) {

        Claims claims = getClaims(token);

        String type = claims.get("type", String.class);

        if (!"RESET_PASSWORD".equals(type)) {
            throw new RuntimeException("Token không hợp lệ");
        }
    }
}