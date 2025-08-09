package com.vaibhav.chatapp.chatapp.controller;

import com.vaibhav.chatapp.chatapp.dto.SendOtpRequest;
import com.vaibhav.chatapp.chatapp.dto.VerifyOtpRequest;
import com.vaibhav.chatapp.chatapp.model.User;
import com.vaibhav.chatapp.chatapp.security.jwt.JwtTokenProvider;
import com.vaibhav.chatapp.chatapp.service.OtpService;
import com.vaibhav.chatapp.chatapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
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
@Tag(name = "Authentication", description = "Phone OTP and JWT login APIs")
public class AuthController {

    @Autowired
    private OtpService otpService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "Send OTP to user phone")
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@Valid @RequestBody SendOtpRequest sendOtpRequest) {
        try {
            String msg = otpService.sendOtp(sendOtpRequest.getPhoneNumber());
            return ResponseEntity.ok(Map.of("message", msg));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Verify OTP and issue JWT tokens")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OTP verified successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid OTP")
    })
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@Valid @RequestBody VerifyOtpRequest verifyOtpRequest, HttpServletResponse response) {
        String status = otpService.verifyOtp(verifyOtpRequest.getPhoneNumber(), verifyOtpRequest.getOtp());

        switch (status) {
            case SUCCESS:
                User user = userService.findOrCreateUser(verifyOtpRequest.getPhoneNumber());
                String token = jwtTokenProvider.generateToken(user.getPhoneNumber(), user.getRole(), 15);
                String refreshToken = jwtTokenProvider.generateToken(user.getPhoneNumber(), user.getRole(), 7*24*60);

                ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                        .httpOnly(true)
                        .secure(false)
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

    @Operation(summary = "Logout user")
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
