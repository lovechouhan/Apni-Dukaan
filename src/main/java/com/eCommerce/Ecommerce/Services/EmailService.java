package com.eCommerce.Ecommerce.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(String userEmail, String otp, String subject, String text) throws MessagingException {
       try{
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
        helper.setFrom("ApniDukaan@gmail.com ");
        helper.setTo(userEmail);
        helper.setSubject(subject);
        helper.setText(text);
        mailSender.send(message);
       } catch (MailException e) {
           throw new RuntimeException("Failed to send email to " + userEmail, e);
       }
    }

    public void receviedquery(String name, String email, String messageBody) throws MessagingException {
        String subject = "New Contact Us Query from " + name;
        String text = "You have received a new query from the contact us form.\n\n"
                + "Name: " + name + "\n"
                + "Email: " + email + "\n"
                + "Message: " + messageBody + "\n";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
            helper.setFrom(email);
            helper.setTo("lovechouhan417@gmail.com");
            helper.setSubject(subject);
            helper.setText(text);
            mailSender.send(message);
            System.out.println("Email sent successfully!");
        } catch (MailException e) {
            throw new RuntimeException("Failed to send contact us query email", e);
        }
    }
}
    
