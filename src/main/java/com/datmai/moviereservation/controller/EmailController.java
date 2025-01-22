package com.datmai.moviereservation.controller;

import com.datmai.moviereservation.service.EmailService;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/email")
@RequiredArgsConstructor
@Slf4j(topic = "EMAIL-CONTROLLER")
public class EmailController {

    private final EmailService emailService;

    @GetMapping("/send")
    public void sendEmail(@RequestParam String to, String subject, String body) {
        log.info("Sending email to {}", to);
        this.emailService.send(to, subject, body);
    }

    @GetMapping("/verification")
    public void emailVerification(@RequestParam String to, String name) throws IOException {
        log.info("Sending email verification toe {}", to);
        this.emailService.emailVerification(to, name);
    }

}