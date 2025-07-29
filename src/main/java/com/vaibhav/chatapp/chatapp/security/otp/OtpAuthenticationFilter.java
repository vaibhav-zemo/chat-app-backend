package com.vaibhav.chatapp.chatapp.security.otp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaibhav.chatapp.chatapp.dto.VerifyOtpRequest;
import com.vaibhav.chatapp.chatapp.security.jwt.JwtTokenProvider;
import com.vaibhav.chatapp.chatapp.util.constants.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class OtpAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public OtpAuthenticationFilter(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            if(!request.getServletPath().equals("/api/auth/verify-otp")){
                filterChain.doFilter(request, response);
                return;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            VerifyOtpRequest verifyOtpRequest = objectMapper.readValue(request.getInputStream(), VerifyOtpRequest.class);

            OtpAuthenticationToken otpAuthenticationToken = new OtpAuthenticationToken(verifyOtpRequest.getPhoneNumber(), verifyOtpRequest.getOtp());
            Authentication authentication = authenticationManager.authenticate(otpAuthenticationToken);

            if(authentication.isAuthenticated()){
                String jwtToken = jwtTokenProvider.generateToken(verifyOtpRequest.getPhoneNumber(), Role.USER,  60*24);
                response.setHeader("Authorization", "Bearer " + jwtToken);
                SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                        authentication.getPrincipal(),
                        authentication.getCredentials(),
                        authentication.getAuthorities()
                ));
                filterChain.doFilter(request, response);
            }
            else response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        catch(Exception e){
            System.out.println("Exception while processing request in OtpAuthenticationFilter: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
