package gr.hua.dit.mycitygov.core.service.impl;

import gr.hua.dit.mycitygov.core.service.EmailService;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for sending emails
 * Currently, is a Mock service that prints to the console
 * TODO: JavaMailSender
 */
@Service
public class EmailServiceImpl implements EmailService {

    /**
     * Simulates sending an email by printing details to the console
     *
     * @param to The recipient's email address
     * @param subject The subject of the email
     * @param body The body content of the email
     */
    @Override
    public void sendEmail(String to, String subject, String body) {
        System.out.println("\n================= EMAIL SENDING =================");
        System.out.println("TO: " + to);
        System.out.println("SUBJECT: " + subject);
        System.out.println("BODY: \n" + body);
        System.out.println("=====================================================\n");
    }
}