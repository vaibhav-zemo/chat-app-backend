package com.vaibhav.chatapp.chatapp.security.jwt;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;


@Getter
public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    private final String token;

    public JwtAuthenticationToken(String token) {
        super(null);
        this.token = token;
        setAuthenticated(false);
    }

    @Override
    public Object getCredentials() {
        return this.token;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }
}
