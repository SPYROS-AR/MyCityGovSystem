package gr.hua.dit.mycitygov.core.security;


import gr.hua.dit.mycitygov.core.model.User;
import gr.hua.dit.mycitygov.core.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementation of Spring's {@code UserDetailsService} for providing application users.
 */
@Service
public class ApplicationUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public ApplicationUserDetailsService(final UserRepository userRepository) {
        if (userRepository == null) throw new NullPointerException();
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        if (username == null) throw new NullPointerException();
        if (username.isBlank()) throw new IllegalArgumentException();
        final User user = this.userRepository
                .findByUsername(username)
                .orElse(null);
        if (user == null) {
            throw new UsernameNotFoundException("user with emailAddress" + username + " does not exist");
        }
        return new ApplicationUserDetails(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(), // saved (hashed) password
                user.getRole()
        );
    }
}
