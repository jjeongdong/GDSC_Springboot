package com.example.todo.config;

import com.example.todo.entity.RefreshToken;
import com.example.todo.entity.User;
import com.example.todo.exception.ExceptionStatus;
import com.example.todo.repository.RefreshTokenRepository;
import com.example.todo.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private int expiration;
    @Value("${jwt.refreshExpiration}")
    private int refreshExpiration;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public String generateAccessToken(String username) {

        Claims claims = Jwts.claims().setSubject(username);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (expiration * 1000L)))
                .signWith(SignatureAlgorithm.HS256, secret.getBytes())
                .compact();
    }

    public String generateRefreshToken(String username) {

        Claims claims = Jwts.claims().setSubject(username);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (refreshExpiration * 1000L)))
                .signWith(SignatureAlgorithm.HS256, secret.getBytes())
                .compact();
    }

    public String createNewAccessToken(String refreshToken){

        if(validateToken(refreshToken)){
            String username = getUsernameFromRefreshToken(refreshToken);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException(String.valueOf(ExceptionStatus.USER_NOT_FOUND)));

            RefreshToken DBRefreshToken = refreshTokenRepository.findTopByUserOrderByIdDesc(user)
                    .orElseThrow(() -> new RuntimeException(String.valueOf(ExceptionStatus.TOKEN_NOT_FOUND)));

            if (Objects.equals(refreshToken, DBRefreshToken.getToken())) {
                return generateAccessToken(username);
            }
        }

        throw new RuntimeException("Invalid RefreshToken");
    }


    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret.getBytes())
                .parseClaimsJws(parseBearer(token))
                .getBody()
                .getSubject();
    }

    public String getUsernameFromRefreshToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret.getBytes())
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            // 토큰 파싱 및 서명 검증
            Jws<Claims> claims = Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(parseBearer(token));

            // 토큰 만료 여부 확인
            Date expirationDate = claims.getBody().getExpiration();
            Date now = new Date();

            // 토큰이 만료되지 않았으면 유효한 토큰으로 간주
            return !expirationDate.before(now);
        } catch (Exception e) {
            // 토큰 유효성 검사 실패 (예: 서명 불일치 또는 만료된 토큰)
            throw new RuntimeException(String.valueOf(ExceptionStatus.INVALID_TOKEN));
        }
    }

    private String parseBearer(String token) {
        return token.replace("Bearer", "").trim();
    }
}
