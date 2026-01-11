package gr.hua.dit.citySmsService.core.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SendSmsRequest(
        @NotNull @NotBlank String phoneNumber,
        @NotNull @NotBlank String message
) {}