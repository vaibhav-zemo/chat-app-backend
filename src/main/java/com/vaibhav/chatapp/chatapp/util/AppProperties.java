package com.vaibhav.chatapp.chatapp.util;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class AppProperties {
    @Value("${chat.relay.host}")
    private String relayHost;

    @Value("${chat.relay.port}")
    private int relayPort;

    @Value("${chat.relay.login}")
    private String relayLogin;

    @Value("${chat.relay.passcode}")
    private String relayPasscode;

    @Value("${chat.relay.virtual-host}")
    private String relayVirtualHost;
}
