package com.steel.product.application.controller;

import com.steel.product.application.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    private final EmailService emailService;

    @Autowired
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("")
    public String sendMail(){
        try {
            emailService.sendEmail();
            return "Sent email ok !!";
        }catch (Exception e){
            e.printStackTrace();
        }
        return "Error in sending mail";
    }
}
