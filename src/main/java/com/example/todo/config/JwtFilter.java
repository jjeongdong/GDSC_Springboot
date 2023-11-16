package com.example.todo.config;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private static final String[] WHITE_LIST = {"/users/signup", "/users/login"};

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.info("Filter Start");

        String requestURI = request.getRequestURI();
        System.out.println(requestURI);

        if (isWhitelistedPath(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {

            String username = jwtTokenProvider.getUsernameFromToken(token);

            if (token != null && jwtTokenProvider.validateToken(token)) {
                request.setAttribute("username", username);
            }

        } catch (ExpiredJwtException e) {

            log.info("Refresh Token을 활용하여 Access Token을 발급합니다.");

            String refreshToken = request.getHeader("Refresh-Token");
            String newAccessToken = jwtTokenProvider.createNewAccessToken(refreshToken);

            response.setHeader("Authorization", "Bearer " + newAccessToken);

        }

        filterChain.doFilter(request, response);
        log.info("Filter End");

    }

    private boolean isWhitelistedPath(String requestURI) {
        return PatternMatchUtils.simpleMatch(WHITE_LIST, requestURI);
    }
}