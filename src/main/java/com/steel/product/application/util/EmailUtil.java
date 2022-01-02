package com.steel.product.application.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.nio.file.Path;

@Service
public class EmailUtil {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailUtil(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(Path path) throws MessagingException {
//        SimpleMailMessage sm = new SimpleMailMessage();
//        sm.setFrom("workeazy8@gmail.com");
//        sm.setTo("mohakrulesenator@gmail.com");
//        sm.setSubject("This is a plain text email");
//        sm.setText("Hello guys! This is a plain text email.");
//        sm.
//        mailSender.send(sm);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setSubject("Aspen private Limited");
        helper.setFrom("mohak.bhatnagar11@gmail.com");
        helper.setTo("mohakrulesenator@gmail.com");

        helper.setText("<b>Hello</b>,<br><i>Please find the file attached.</i>", true);

        FileSystemResource file = new FileSystemResource(path);
        helper.addAttachment(path.getFileName().toString(), file);

        mailSender.send(message);

    }
}
