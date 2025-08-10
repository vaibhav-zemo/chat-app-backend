package com.vaibhav.chatapp.chatapp.security.jwt;

import com.vaibhav.chatapp.chatapp.model.User;
import com.vaibhav.chatapp.chatapp.service.UserService;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    public JwtAuthenticationProvider(JwtTokenProvider jwtTokenProvider, UserService userService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = authentication.getCredentials().toString();

        Claims claims = jwtTokenProvider.validateAndGetClaims(token);
        String phoneNumber = claims.getSubject();
        String role = claims.get("role", String.class);
        if(phoneNumber == null) {
            throw new BadCredentialsException("Invalid token");
        }

        User user = userService.findByPhoneNumber(phoneNumber);
        return new UsernamePasswordAuthenticationToken(user, null, List.of(new SimpleGrantedAuthority(role)));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
