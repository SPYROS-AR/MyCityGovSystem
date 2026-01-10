package gr.hua.dit.mycitygov.core.service.model;

import gr.hua.dit.mycitygov.core.model.User;

public record UserView (
    Long  id,
    String firstName,
    String lastName,
    String username,
    String email,
    String password,
    User.Role role
){}
