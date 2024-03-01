package com.optimised.services;

import jakarta.mail.MessagingException;

import java.io.IOException;

/**
 * Created by Olga on 8/22/2016.
 */
public interface EmailService {
    String sendSimpleMessage(String to,
                           String subject,
                           String text);
    String sendMessageWithAttachment(String to,
                                   String subject,
                                   String text,
                                   String pathToAttachment)
            throws IOException, MessagingException;

}