package gr.hua.dit.mycitygov.core.security;

import gr.hua.dit.mycitygov.core.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Immutable view implementing Spring's {@link UserDetails} for representing a user in runtime.
 */
@SuppressWarnings("RedundantMethodOverride")
public final class ApplicationUserDetails implements UserDetails {

    private final long personId;
    private final String firstName;
    private final String lastName;
    private final String username;
    private final String emailAddress;
    private final String passwordHash;
    private final User.Role role;

    public ApplicationUserDetails(final long personId,
                                  final String firstName,
                                  final String lastName,
                                  final String username,
                                  final String emailAddress,
                                  final String passwordHash,
                                  final User.Role role) {
        if (personId <= 0) throw new IllegalArgumentException();
        if (username == null) throw new NullPointerException();
        if (username.isBlank()) throw new IllegalArgumentException();
        if (emailAddress == null) throw new NullPointerException();
        if (emailAddress.isBlank()) throw new IllegalArgumentException();
        if (passwordHash == null) throw new NullPointerException();
        if (passwordHash.isBlank()) throw new IllegalArgumentException();
        if (role == null) throw new NullPointerException();

        this.personId = personId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.emailAddress = emailAddress;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public long personId() {
        return this.personId;
    }
    public String firstName() {
        return this.firstName;
    }
    public String lastName() {
        return this.lastName;
    }

    public String getEmail() {
        return this.emailAddress;
    }

    public User.Role role() {
        return this.role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return this.passwordHash;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
