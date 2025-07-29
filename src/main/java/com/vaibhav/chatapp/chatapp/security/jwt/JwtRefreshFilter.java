package com.vaibhav.chatapp.chatapp.security.jwt;

import com.vaibhav.chatapp.chatapp.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

public class JwtRefreshFilter extends OncePerRequestFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public JwtRefreshFilter(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            if(!request.getServletPath().equals("/api/auth/refresh-token")){
                filterChain.doFilter(request, response);
                return;
            }

            String refreshToken = extractJwtFromRequest(request);
            if(refreshToken == null){
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(refreshToken);
            Authentication authResult = authenticationManager.authenticate(authenticationToken);
            if(authResult.isAuthenticated()){
                User user = (User) authResult.getPrincipal();
                String newToken = jwtTokenProvider.generateToken(user.getPhoneNumber(), user.getRole(), 15);
                String newRefreshToken = jwtTokenProvider.generateToken(user.getPhoneNumber(), user.getRole(), 60*24*7);

                ResponseCookie cookie = ResponseCookie.from("refreshToken", newRefreshToken)
                        .httpOnly(true)
                        .secure(false)
                        .sameSite("Strict")
                        .path("/api/auth/refresh-token")
                        .maxAge(Duration.ofDays(7))
                        .build();

                response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
                response.addHeader("Authorization", "Bearer " + newToken);
            }
        }
        catch(Exception e){
            System.out.println("Exception in jwtRefreshFilter: " + e.getMessage());
        }
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        String refreshToken = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refreshToken")) {
                refreshToken = cookie.getValue();
            }
        }

        return refreshToken;
    }
}
