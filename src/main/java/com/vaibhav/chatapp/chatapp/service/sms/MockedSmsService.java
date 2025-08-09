package com.vaibhav.chatapp.chatapp.service.sms;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("dev")
public class MockedSmsService implements SmsService {
    @Override
    public void sendSms(String phoneNumber, String otp) {
        System.out.println("Generated OTP: " + otp);
    }
}
