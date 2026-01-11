package gr.hua.dit.citySmsService.core.impl;

import gr.hua.dit.citySmsService.core.SmsService;
import gr.hua.dit.citySmsService.core.model.SendSmsRequest;
import gr.hua.dit.citySmsService.core.model.SendSmsResult;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * Mock implementation of {@link SmsService}
 * <p>
 * This implementation does not actually send SMS messages but logs the request details
 * and returns a successful result
 * </p>
 */
@Service
@Primary
public class MockSmsService implements SmsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MockSmsService.class);

    /**
     * Simulates sending an SMS by logging the phone number and message to the console
     *
     * @param sendSmsRequest the SMS request detail
     * @return a successful {@link SendSmsResult} with status "OK"
     * @throws NullPointerException     if the request or its fields are null
     * @throws IllegalArgumentException if the fields are blank
     */
    @Override
    public SendSmsResult send(@Valid final SendSmsRequest sendSmsRequest) {
        if (sendSmsRequest == null) throw new NullPointerException();

        final String phoneNumber = sendSmsRequest.phoneNumber();
        final String message = sendSmsRequest.message();

        if (phoneNumber == null) throw new NullPointerException();
        if (phoneNumber.isBlank()) throw new IllegalArgumentException();
        if (message == null) throw new NullPointerException();
        if (message.isBlank()) throw new IllegalArgumentException();

        LOGGER.info("SENDING SMS {} {}", phoneNumber, message);

        return new SendSmsResult("OK");
    }


}