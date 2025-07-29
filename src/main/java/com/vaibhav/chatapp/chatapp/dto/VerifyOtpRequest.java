package com.vaibhav.chatapp.chatapp.dto;


import lombok.*;

@Data
public class VerifyOtpRequest {
    private String phoneNumber;
    private String otp;
}
