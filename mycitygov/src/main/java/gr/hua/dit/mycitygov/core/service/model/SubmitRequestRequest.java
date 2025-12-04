package gr.hua.dit.mycitygov.core.service.model;

import jakarta.validation.constraints.NotBlank;

public record SubmitRequestRequest(
        @NotBlank Long requestTypeId,
        @NotBlank String description
) {}
