package gr.hua.dit.mycitygov.core.service.model;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCitizenRequest(
        @NotBlank @Size(max = 50) String username,
        @NotBlank String rawPassword,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank @Email String email,
        @NotBlank @Size(max = 9) String nationalId,
        @NotBlank @Size(max = 18) String mobilePhoneNumber,
        @NotBlank String address
) {
}
