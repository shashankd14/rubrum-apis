package com.steel.product.application.util;

import com.steel.product.application.dto.email.EmailProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

@Service
public class EmailUtil {

    private final static Logger LOGGER = LoggerFactory.getLogger(EmailUtil.class);

    private final JavaMailSender mailSender;
    private final EmailProperties emailProperties;

    @Autowired
    public EmailUtil(JavaMailSender mailSender, EmailProperties emailProperties) {
        this.mailSender = mailSender;
        this.emailProperties = emailProperties;
    }

    public void sendEmail(File report,String to) {
        LOGGER.info("sending stock report email with attachment to "+to);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setSubject(emailProperties.getSubject());
            helper.setTo(to);
            helper.setText(emailProperties.getText(), emailProperties.getIsHTML());
            helper.addAttachment(report.getName(), report);
            mailSender.send(message);
        }catch (MessagingException e){
            e.printStackTrace();
        }
        LOGGER.info("email sent ok ");
    }
}
