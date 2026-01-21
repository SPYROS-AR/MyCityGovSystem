package gr.hua.dit.mycitygov.core.service.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record SubmitRequestRequest(
        @NotNull Long requestTypeId,
        @NotBlank String description,

        String requiredDocuments,
        MultipartFile attachment
) {}