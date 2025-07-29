package com.vaibhav.chatapp.chatapp.controller;

import com.vaibhav.chatapp.chatapp.dto.VerifyOtpRequest;
import com.vaibhav.chatapp.chatapp.model.User;
import com.vaibhav.chatapp.chatapp.security.jwt.JwtTokenProvider;
import com.vaibhav.chatapp.chatapp.service.OtpService;
import com.vaibhav.chatapp.chatapp.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

import static com.vaibhav.chatapp.chatapp.util.constants.OtpStatus.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private OtpService otpService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestParam String phoneNumber) {
        try {
            String msg = otpService.sendOtp(phoneNumber);
            return ResponseEntity.ok(Map.of("message", msg));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequest verifyOtpRequest, HttpServletResponse response) {
        String status = otpService.verifyOtp(verifyOtpRequest.getPhoneNumber(), verifyOtpRequest.getOtp());

        switch (status) {
            case SUCCESS:
                User user = userService.findOrCreateUser(verifyOtpRequest.getPhoneNumber());
                String token = jwtTokenProvider.generateToken(user.getPhoneNumber(), user.getRole(), 15);
                String refreshToken = jwtTokenProvider.generateToken(user.getPhoneNumber(), user.getRole(), 7*24*60);

                ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                        .httpOnly(true)
                        .secure(false) // set false only if testing on localhost without https
                        .sameSite("Strict")
                        .path("/api/auth/refresh-token")
                        .maxAge(Duration.ofDays(7))
                        .build();

                response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

                return ResponseEntity.ok(Map.of("message", "OTP verified successfully" , "token", token, "userId", user.getUserId()));
            case INVALID:
                return ResponseEntity.status(401).body(Map.of("error", "Invalid OTP"));
            case EXPIRED:
                return ResponseEntity.status(401).body(Map.of("error", "OTP expired"));
            case BLOCKED:
                return ResponseEntity.status(403).body(Map.of("error", "Too many invalid attempts, try later"));
            default:
                return ResponseEntity.status(500).body(Map.of("error", "Unexpected error"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/api/auth/refresh-token")
                .maxAge(0)
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok("Logged out successfully");
    }
}
