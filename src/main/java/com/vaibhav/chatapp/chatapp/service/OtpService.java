package com.vaibhav.chatapp.chatapp.service;

import com.vaibhav.chatapp.chatapp.service.sms.SmsService;
import com.vaibhav.chatapp.chatapp.util.Utilities;
import com.vaibhav.chatapp.chatapp.util.constants.OtpStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
public class OtpService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private @Qualifier("authKeySmsService") SmsService smsService;

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 5;
    private static final int COOLDOWN_SECONDS = 30;
    private static final int MAX_REQUESTS_PER_HOUR = 5;
    private static final int MAX_VERIFY_ATTEMPTS = 5;

    public String sendOtp(String phoneNumber) {
        try{
            String cooldownKey = "OTP_COOLDOWN_" + phoneNumber;
            String countKey = "OTP_REQ_COUNT_" + phoneNumber;
            String otpKey = "OTP_" + phoneNumber;

            // 1. Check cooldown (30 sec)
            if (redisService.keyExists(cooldownKey)) {
                throw new RuntimeException("Please wait 30 seconds before requesting another OTP");
            }

            // 2. Limit requests per hour
            Long count = redisService.increment(countKey);
            if (count == 1) {
                redisService.expire(countKey, Duration.ofHours(1));
            }
            if (count > MAX_REQUESTS_PER_HOUR) {
                throw new RuntimeException("Maximum OTP requests reached for this hour. Try again later.");
            }

            String otp = Utilities.generateOtp(OTP_LENGTH);

            // 4. Store OTP with TTL (5 min)
            redisService.setValue(otpKey, otp, Duration.ofMinutes(OTP_EXPIRY_MINUTES));

            // 5. Reset attempt counter for this OTP
            redisService.setValue("OTP_ATTEMPTS_" + phoneNumber, 0, Duration.ofMinutes(OTP_EXPIRY_MINUTES));

            // 6. Set cooldown
            redisService.setValue(cooldownKey, "1", Duration.ofSeconds(COOLDOWN_SECONDS));

            // 7. Send via SMS provider
//            smsService.sendSms(phoneNumber, otp);
            System.out.println("OTP is: " + otp);

            return "OTP sent successfully";
        }
        catch(Exception e){
            log.error("Error while sending otp: {}", e.getMessage());
            return e.getMessage();
        }
    }

    public String verifyOtp(String phoneNumber, String otp) {
        try{
            String otpKey = "OTP_" + phoneNumber;
            String attemptKey = "OTP_ATTEMPTS_" + phoneNumber;

            // 1. Check if OTP exists
            if (!redisService.keyExists(otpKey)) {
                return OtpStatus.EXPIRED;
            }

            String actualOtp = redisService.getValue(otpKey).toString();

            // 2. Check attempt count
            Long attempts = redisService.increment(attemptKey);
            if (attempts == 1) {
                redisService.expire(attemptKey, Duration.ofMinutes(OTP_EXPIRY_MINUTES));
            }
            if (attempts > MAX_VERIFY_ATTEMPTS) {
                redisService.deleteKey(otpKey); // expire OTP early
                return OtpStatus.BLOCKED;
            }

            if (constantTimeEquals(actualOtp, otp)) {
                redisService.deleteKey(otpKey);
                redisService.deleteKey(attemptKey);
                return OtpStatus.SUCCESS;
            } else {
                return OtpStatus.INVALID;
            }
        }
        catch (Exception e){
            log.error("Error while verify otp: {}", e.getMessage());
        }
        return null;
    }

    private boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) return false;
        if (a.length() != b.length()) return false;

        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}
