package com.vaibhav.chatapp.chatapp.util;

import java.security.SecureRandom;

public class Utilities {

    public static String generateOtp(int otpLength) {
        SecureRandom random = new SecureRandom();
        int number = random.nextInt((int) Math.pow(10, otpLength));
        return String.format("%0" + otpLength + "d", number);
    }
}
