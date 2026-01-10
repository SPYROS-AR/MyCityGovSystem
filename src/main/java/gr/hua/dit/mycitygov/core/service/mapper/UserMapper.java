package gr.hua.dit.mycitygov.core.service.mapper;

import gr.hua.dit.mycitygov.core.model.User;
import gr.hua.dit.mycitygov.core.service.model.UserView;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserView toDto(User user) {
        if (user == null) return null;

        return new UserView(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getRole()
        );

    }
}
