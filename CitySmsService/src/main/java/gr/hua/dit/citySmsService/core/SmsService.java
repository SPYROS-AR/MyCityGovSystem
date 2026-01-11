package gr.hua.dit.citySmsService.core;

import gr.hua.dit.citySmsService.core.model.SendSmsRequest;
import gr.hua.dit.citySmsService.core.model.SendSmsResult;

public interface SmsService {
    SendSmsResult send(final SendSmsRequest sendSmsRequest);
}