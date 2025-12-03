package gr.hua.dit.mycitygov.core.service.model;


public record CreateCitizenRequest(
        String username,
        String rawPassword,
        String firstName,
        String lastName,
        String email,
        String mationalId,
        String mobilePhoneNumber,
        String address
) {
}
