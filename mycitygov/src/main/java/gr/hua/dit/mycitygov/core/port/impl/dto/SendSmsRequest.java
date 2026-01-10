package gr.hua.dit.mycitygov.core.port.impl.dto;

public class SendSmsRequest {
    private String phoneNumber;
    private String message;

    public SendSmsRequest() {}

    public SendSmsRequest(String phoneNumber, String message) {
        this.phoneNumber = phoneNumber;
        this.message = message;
    }

    // Getters & Setters
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
