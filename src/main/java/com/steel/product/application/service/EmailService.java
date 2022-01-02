package com.steel.product.application.service;

import javax.mail.MessagingException;

public interface EmailService {

    void sendEmail() throws MessagingException;
}
