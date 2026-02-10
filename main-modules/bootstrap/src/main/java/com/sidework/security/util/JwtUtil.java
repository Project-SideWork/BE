package com.sidework.security.util;


import com.sidework.security.exception.InvalidTokenException;
import com.sidework.user.application.port.out.UserOutPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {

    private static final long ACCESS_TOKEN_EXPIRE_TIME = 30 * 60 * 1000; // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 60 * 60 * 1000; // 1시
    //객체 키 생성
    private SecretKey secretKey;

    //검증 메서드

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getEmail(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
                .get("email", String.class);
    }

    public String getCategory(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
                .get("category", String.class);
    }

    public Boolean isExpired(String token) {
        try {
            Claims claims = extractClaims(token);
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());

        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            throw new InvalidTokenException();
        }
    }

    public String createAccess(String email) {
        return Jwts.builder()
                .claim("category", "access")
                .claim("email", email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE_TIME))
                .signWith(secretKey)
                .compact();
    }

    public String createRefresh(String email) {
        return Jwts.builder()
                .claim("category", "refresh")
                .claim("email", email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(secretKey)
                .compact();
    }

    private Claims extractClaims(String token){
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseSignedClaims(token)
                .getBody();

    }

    public long getRefreshTokenExpireTime(String refreshToken) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(refreshToken)
                    .getBody();

            Date expiration = claims.getExpiration();
            if (expiration == null) return 0L;

            Instant exp = expiration.toInstant();
            return Math.max(Duration.between(Instant.now(), exp).getSeconds(), 0);
        } catch (Exception e) {
            return 0L;
        }
    }
}
