package com.vaibhav.chatapp.chatapp.security.otp;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.stereotype.Component;

import javax.security.auth.Subject;

@Getter
public class OtpAuthenticationToken extends AbstractAuthenticationToken {
    private final String phoneNumber;
    private final String otp;

    public OtpAuthenticationToken(String phoneNumber, String otp) {
        super(null);
        this.phoneNumber = phoneNumber;
        this.otp = otp;
        setAuthenticated(false);
    }

    @Override
    public Object getCredentials() {
        return this.otp;
    }

    @Override
    public Object getPrincipal() {
        return this.phoneNumber;
    }
}
