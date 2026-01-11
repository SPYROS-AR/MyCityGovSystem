package gr.hua.dit.citySmsService.core.impl;

import gr.hua.dit.citySmsService.core.SmsService;
import gr.hua.dit.citySmsService.core.model.SendSmsRequest;
import gr.hua.dit.citySmsService.core.model.SendSmsResult;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class MockSmsService implements SmsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MockSmsService.class);

    @Override
    public SendSmsResult send(@Valid final SendSmsRequest sendSmsRequest) {
        if (sendSmsRequest == null) throw new NullPointerException();

        // --------------------------------------------------

        final String phoneNumber = sendSmsRequest.phoneNumber();
        final String message = sendSmsRequest.message();

        // --------------------------------------------------

        if (phoneNumber == null) throw new NullPointerException();
        if (phoneNumber.isBlank()) throw new IllegalArgumentException();
        if (message == null) throw new NullPointerException();
        if (message.isBlank()) throw new IllegalArgumentException();

        // --------------------------------------------------

        LOGGER.info("SENDING SMS {} {}", phoneNumber, message);

        // --------------------------------------------------

        return new SendSmsResult(false);
    }


}