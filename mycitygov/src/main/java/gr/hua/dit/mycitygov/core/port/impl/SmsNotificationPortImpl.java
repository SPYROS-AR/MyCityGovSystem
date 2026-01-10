package gr.hua.dit.mycitygov.core.port.impl;

import gr.hua.dit.mycitygov.core.port.SmsNotificationPort;
import gr.hua.dit.mycitygov.core.port.impl.dto.SendSmsRequest;
import gr.hua.dit.mycitygov.core.port.impl.dto.SendSmsResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class SmsNotificationPortImpl implements SmsNotificationPort {

    private final RestClient restClient;

    @Value("${sms.service.url}")
    private String smsServiceUrl;

    public SmsNotificationPortImpl() {
        this.restClient = RestClient.create();
    }

    @Override
    public boolean sendSms(String phoneNumber, String message) {
        try {
            SendSmsRequest request = new SendSmsRequest(phoneNumber, message);

            SendSmsResult result = restClient.post()
                    .uri(smsServiceUrl + "/api/v1/sms")
                    .body(request)
                    .retrieve()
                    .body(SendSmsResult.class);

            return result != null && "OK".equalsIgnoreCase(result.getStatus());

        } catch (Exception e) {
            System.err.println("Failed to send SMS to " + phoneNumber + ": " + e.getMessage());
            return false;
        }
    }
}
