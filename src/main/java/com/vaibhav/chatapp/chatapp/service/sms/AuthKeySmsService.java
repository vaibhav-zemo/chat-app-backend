package com.vaibhav.chatapp.chatapp.service.sms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service("authKeySmsService")
public class AuthKeySmsService implements SmsService {

    @Value("${otp.mobile.api}")
    private String authkey;

    @Value("${otp.mobile.senderId}")
    private String senderId;

    @Override
    public void sendSms(String phoneNumber, String otp) {
        try{
            String countryCode = "+91";
            String company = "ChatApp";

            String url = String.format(
                    "https://api.authkey.io/request?authkey=%s&mobile=%s&country_code=%s&sid=%s&company=%s&otp=%s",
                    authkey, phoneNumber, countryCode, senderId, company, otp
            );

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getForObject(url, String.class);
            log.info("OTP sent via Authkey.io to {}", phoneNumber);
        }
        catch(Exception e){
            log.error("Error while calling url of authkey.io: {}", e.getMessage());
        }
    }
}
