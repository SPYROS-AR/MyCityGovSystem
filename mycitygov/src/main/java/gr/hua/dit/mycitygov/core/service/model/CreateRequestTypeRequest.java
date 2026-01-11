package gr.hua.dit.mycitygov.core.service.model;

import gr.hua.dit.mycitygov.core.model.RequestType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


/**
 * DTO for creating new RequestType Admin.
 */
public record CreateRequestTypeRequest(
        @NotBlank
        @Size(max = 100)
        String name,

        @NotNull
        RequestType.RequestCategory requestCategory,

        @NotNull
        @Min(1)
        Integer slaDays,

        @NotNull
        Long departmentId,

        String requiredDocuments
) {}
