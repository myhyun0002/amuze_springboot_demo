package com.amuse.www.demo.jwt;

import com.amuse.www.demo.domain.item.DateConvertor;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProperties jwtProperties;

    // jwt 토큰에서 이름 추출
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // jwt 토큰에서 토큰 유효 기간 추출
    public LocalDateTime extractExpiration(String token) {
        return DateConvertor.toLocalDateTime(extractClaim(token, Claims::getExpiration));
    }

    // 토큰에서 정보 추출
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 토큰에서 모든 정보 추출
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(jwtProperties.getSecretKey()).parseClaimsJws(token).getBody();
    }

    // 토큰이 유효 기간이 지났는지 보여준다.
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).isBefore(LocalDateTime.now());
    }

    // 토큰 생성
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return generateToken(claims, username, jwtProperties.getTokenExpired());
    }

    private String generateToken(Map<String, Object> claims, String subject, Long expiryTime) {
        LocalDateTime expiryDate = LocalDateTime.now().plusSeconds(expiryTime);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(DateConvertor.toDate(LocalDateTime.now()))
                .setExpiration(DateConvertor.toDate(expiryDate))
                .signWith(jwtProperties.getSignatureAlgorithm(), jwtProperties.getSecretKey())
                .compact();
    }

    // 토큰 유효한지 검사
    public boolean validateToken(String token, String username) {
        final String tokenUsername = extractUsername(token);
        return (username.equals(tokenUsername) && !isTokenExpired(token));
    }

    // 토큰의 유효 기간 추출
    public Long getTokenExpirationDate() {
        return jwtProperties.getTokenExpired();
    }

}
