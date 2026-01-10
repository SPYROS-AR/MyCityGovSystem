package gr.hua.dit.mycitygov.core.port;

public interface SmsNotificationPort {
    /**
     * Sends an SMS notification
     * @param phoneNumber The recipient's phone number (e.g., 69XXXXXXXX)
     * @param message The content of the SMS
     * @return true if successful, false otherwise
     */
    boolean sendSms(String phoneNumber, String message);
}