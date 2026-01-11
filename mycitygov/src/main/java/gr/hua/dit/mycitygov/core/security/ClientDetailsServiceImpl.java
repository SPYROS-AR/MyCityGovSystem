package gr.hua.dit.mycitygov.core.security;

import gr.hua.dit.mycitygov.core.model.Client;
import gr.hua.dit.mycitygov.core.repository.ClientRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClientDetailsServiceImpl implements ClientDetailsService {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder; // 2. Προσθήκη field

    public ClientDetailsServiceImpl(ClientRepository clientRepository, PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder; // Injection μέσω constructor
    }

    @Override
    public Optional<ClientDetails> authenticate(final String id, final String secret) {
        // Αναζήτηση client
        return this.clientRepository.findByName(id)
                // 3. Χρήση της matches() αντί για Objects.equals()
                .filter(client -> passwordEncoder.matches(secret, client.getSecret()))
                .map(client -> new ClientDetails(
                        client.getName(),
                        client.getSecret(),
                        Arrays.stream(client.getRolesCsv().split(","))
                                .map(String::strip)
                                .map(String::toUpperCase)
                                .collect(Collectors.toSet())
                ));
    }
}
