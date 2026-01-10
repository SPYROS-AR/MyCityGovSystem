package gr.hua.dit.mycitygov.core.port.impl;

import gr.hua.dit.mycitygov.core.port.EmailNotificationPort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationPortImpl implements EmailNotificationPort {
    private final JavaMailSender mailSender;

    public EmailNotificationPortImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public boolean sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            message.setFrom("no-reply@mycitygov.gr");
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            System.err.println("Error sending email");
            return false;
        }
    }
}
