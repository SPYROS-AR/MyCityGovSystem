package gr.hua.dit.mycitygov.core.port;

/**
 * Port for sending email notifications
 * Following the hexagonal architecture pattern from lab exercises
 */
public interface EmailNotificationPort {
    /**
     * Sends email
     * @param to Recipient email
     * @param subject Email subject
     * @param body Email body
     * @return true if sent
     */
    boolean sendEmail(String to, String subject, String body);
}
