package gr.hua.dit.mycitygov.core.port.impl.dto;

/**
 * Data Transfer Object (DTO) representing the response received from the external SMS service
 */
public class SendSmsResult {
    /**
     * The status of the SMS operation ("OK" or "FAILED")
     */
    private String status;

    public SendSmsResult() {}

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
