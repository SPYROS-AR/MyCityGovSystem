package gr.hua.dit.mycitygov.core.port.impl;

import gr.hua.dit.mycitygov.core.port.SmsNotificationPort;
import gr.hua.dit.mycitygov.core.port.impl.dto.SendSmsRequest;
import gr.hua.dit.mycitygov.core.port.impl.dto.SendSmsResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Implementation of the {@link SmsNotificationPort} that communicates with an external SMS REST service
 * <p>
 * This component acts as an adapter, translating the internal domain call into
 * an HTTP POST request to the configured external service URL
 * </p>
 */
@Component
public class SmsNotificationPortImpl implements SmsNotificationPort {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmsNotificationPortImpl.class);
    private final RestClient restClient;

    @Value("${sms.service.url}")
    private String smsServiceUrl;

    @Value("${sms.service.token}")
    private String serviceToken;

    /**
     * Constructs the SmsNotificationPortImpl and initializes the RestClient
     */
    public SmsNotificationPortImpl() {
        this.restClient = RestClient.create();
    }

    /**
     * Sends an SMS by making a REST API call to the external SMS service
     *
     * @param phoneNumber The recipient's phone number
     * @param message     The message content
     * @return {@code true} if the external service returns a success status ("OK")
     * {@code false} if the request fails or the service returns an error
     */
    @Override
    public boolean sendSms(String phoneNumber, String message) {
        try {
            SendSmsRequest request = new SendSmsRequest(phoneNumber, message);

            SendSmsResult result = restClient.post()
                    .uri(smsServiceUrl + "/api/v1/sms")
                    .header("Authorization", "Bearer " + serviceToken)
                    .body(request)
                    .retrieve()
                    .body(SendSmsResult.class);

            boolean isSuccess = result != null && "OK".equalsIgnoreCase(result.getStatus());

            // for logs
            if (isSuccess) {
                LOGGER.info("Successfully requested SMS for phone number: {}", phoneNumber);
            } else {
                LOGGER.warn("SMS service returned non-OK status for number: {}", phoneNumber);
            }
            return isSuccess;

        } catch (Exception e) {
            LOGGER.error("Failed to communicate with SMS service for {}: {}", phoneNumber, e.getMessage());
            return false;
        }
    }
}
