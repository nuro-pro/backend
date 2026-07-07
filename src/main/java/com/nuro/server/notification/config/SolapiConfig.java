package com.nuro.server.notification.config;

import com.solapi.sdk.NurigoApp;
import com.solapi.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SolapiConfig {
    @Bean
    public DefaultMessageService messageService(
            @Value("${solapi.api-key}") String apiKey,
            @Value("${solapi.api-secret}") String apiSecret) {
        return NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.solapi.com");
    }
}
