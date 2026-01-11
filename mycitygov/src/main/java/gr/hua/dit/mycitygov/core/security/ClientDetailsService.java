package gr.hua.dit.mycitygov.core.security;

import java.util.Optional;

/**
 * Service for managing REST API (integration) Client authentication.
 */
public interface ClientDetailsService {
    Optional<ClientDetails> authenticate(final String id, final String secret);
}
