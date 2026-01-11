package gr.hua.dit.citySmsService.core.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Domain object representing a request to send an SMS
 *
 * @param phoneNumber the destination phone number (must not be null or blank)
 * @param message     the text content of the SMS (must not be null or blank)
 */
public record SendSmsRequest(
        @NotNull @NotBlank String phoneNumber,
        @NotNull @NotBlank String message
) {}