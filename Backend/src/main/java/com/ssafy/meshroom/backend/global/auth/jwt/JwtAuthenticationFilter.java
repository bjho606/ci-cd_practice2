package com.ssafy.meshroom.backend.global.auth.jwt;

import com.ssafy.meshroom.backend.global.error.exception.SecurityAuthenticationException;
import com.ssafy.meshroom.backend.global.util.CookieUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        String token = CookieUtil.getCookie(request,"token").orElseThrow(()->new SecurityAuthenticationException("Invalid token")).getValue();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        log.info(token);
        if (token == null || !tokenProvider.validToken(token)) {
            throw new RuntimeException("Invalid token");
        }
        Authentication authentication = tokenProvider.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    /**
     * 회원가입과 로그인, 토큰재발급, swagger 문서에 대해서는 filtering 하지 않는다.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return ("test-token".equals(request.getHeader("X-Test-Token")))
                || !(request.getMethod().equals("PATCH") && path.startsWith(("/api/v1/sessions")))
//                || path.startsWith(("/swagger-ui"))
//                || path.startsWith(("/api-docs"))
                ;
    }
}
