package gr.hua.dit.mycitygov.core.service.model;


import gr.hua.dit.mycitygov.core.model.User;

/**
 * DTO
 */
public record CitizenView(
    long id,
    String username,
    String firstName,
    String lastName,
    User.Role Role,
    String email,
    String mationalId,
    String mobilePhoneNumber,
    String address
    )
{}

