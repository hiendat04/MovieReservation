package com.datmai.moviereservation.config;

import com.sendgrid.SendGrid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class EmailConfiguration {

    @Bean
    public SendGrid sendGrid(@Value("${spring.sendGrid.apiKey}") String apiKey) {
        log.info("SendGrid apiKey: {}", apiKey);
        return new SendGrid(apiKey);
    }
}