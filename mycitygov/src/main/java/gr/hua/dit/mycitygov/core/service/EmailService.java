package gr.hua.dit.mycitygov.core.service;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
}
