package com.mall.security.component;

import com.mall.common.constant.CommonConstant;
import com.mall.common.util.RedisUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtTokenUtil {

    private final RedisUtil redisUtil;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username, Map<String, Object> claims, String type) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expiration * 1000);
        String token = Jwts.builder()
                .subject(username)
                .claims(claims)
                .issuedAt(now)
                .expiration(expireDate)
                .signWith(getSigningKey())
                .compact();
        String redisKey = buildRedisKey(username, type);
        redisUtil.set(redisKey, token, expiration);
        return token;
    }

    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    public boolean validateToken(String token, String type) {
        try {
            if (isTokenExpired(token)) {
                return false;
            }
            String username = getUsernameFromToken(token);
            String redisKey = buildRedisKey(username, type);
            String storedToken = redisUtil.get(redisKey);
            return token.equals(storedToken);
        } catch (JwtException e) {
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expirationDate = getClaimsFromToken(token).getExpiration();
            return expirationDate.before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }

    public String refreshToken(String token, String type) {
        Claims claims = getClaimsFromToken(token);
        Date expirationDate = claims.getExpiration();
        long remainingSeconds = (expirationDate.getTime() - System.currentTimeMillis()) / 1000;
        if (remainingSeconds > 0 && remainingSeconds <= 1800) {
            String username = claims.getSubject();
            return generateToken(username, claims, type);
        }
        return null;
    }

    public void removeToken(String username, String type) {
        String redisKey = buildRedisKey(username, type);
        redisUtil.delete(redisKey);
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private String buildRedisKey(String username, String type) {
        if ("admin".equals(type)) {
            return CommonConstant.REDIS_TOKEN_PREFIX_ADMIN + username;
        }
        return CommonConstant.REDIS_TOKEN_PREFIX_MEMBER + username;
    }
}
