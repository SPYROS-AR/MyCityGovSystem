package gr.hua.dit.mycitygov.core.port;

/**
 * Interface defining the contract for sending SMS notifications
 * <p>
 * This port allows the core application to send messages without knowing
 * the underlying implementation details (e.g., REST call, etc.)
 * </p>
 */
public interface SmsNotificationPort {
    /**
     * Sends an SMS notification
     * @param phoneNumber The recipient's phone number
     * @param message The content of the SMS
     * @return true if successful, false otherwise
     */
    boolean sendSms(String phoneNumber, String message);
}