package com.vaibhav.chatapp.chatapp.security.otp;

import com.vaibhav.chatapp.chatapp.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OtpAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    RedisService redisService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String phoneNumber = authentication.getPrincipal().toString();
        String otp = authentication.getCredentials().toString();

        String redisOtp = redisService.keyExists(phoneNumber) ? redisService.getValue(phoneNumber).toString() : null;
        if (redisOtp == null || !redisOtp.equals(otp)) {
            throw new BadCredentialsException("Invalid OTP");
        }

        return new UsernamePasswordAuthenticationToken(phoneNumber, null,  List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OtpAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
