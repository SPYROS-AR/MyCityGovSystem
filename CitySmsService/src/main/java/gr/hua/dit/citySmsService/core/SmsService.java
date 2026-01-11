package gr.hua.dit.citySmsService.core;

import gr.hua.dit.citySmsService.core.model.SendSmsRequest;
import gr.hua.dit.citySmsService.core.model.SendSmsResult;

/**
 * Core service interface for SMS operations
 */
public interface SmsService {
    /**
     * Sends an SMS based on the provided request
     *
     * @param sendSmsRequest the SMS details (recipient and message)
     * @return the result of the send operation
     */
    SendSmsResult send(final SendSmsRequest sendSmsRequest);
}