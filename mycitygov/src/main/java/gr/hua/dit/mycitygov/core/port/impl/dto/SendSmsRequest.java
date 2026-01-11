package gr.hua.dit.mycitygov.core.port.impl.dto;

/**
 * Data Transfer Object (DTO) representing the payload sent to the external SMS service
 */
public class SendSmsRequest {
    private String phoneNumber;
    private String message;

    public SendSmsRequest() {}

    /**
     * Constructs a new SendSmsRequest
     *
     * @param phoneNumber The recipient's phone number
     * @param message     The message content
     */
    public SendSmsRequest(String phoneNumber, String message) {
        this.phoneNumber = phoneNumber;
        this.message = message;
    }

    // Getters, Setters
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
