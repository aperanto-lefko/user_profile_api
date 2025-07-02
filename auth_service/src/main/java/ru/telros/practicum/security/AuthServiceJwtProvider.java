package ru.telros.practicum.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import ru.telros.practicum.entity.Account;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
@Slf4j
public class AuthServiceJwtProvider {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    public String generateToken(Authentication authentication) {
        Account account = (Account) authentication.getPrincipal();
        String username = authentication.getName();
        UUID accountId = account.getId();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .claim("accountId", accountId.toString())
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.ExpiredJwtException e) {

            log.error("Срок действия токена JWT истек: {}", token, e);
        } catch (io.jsonwebtoken.MalformedJwtException e) {

            log.error("Токен JWT имеет неправильный формат: {}", token, e);
        } catch (io.jsonwebtoken.SignatureException e) {

            log.error("Подпись JWT недействительна: {}", token, e);
        } catch (Exception e) {
            log.error("Недействительный токен JWT: {}", token, e);
        }
        return false;
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
