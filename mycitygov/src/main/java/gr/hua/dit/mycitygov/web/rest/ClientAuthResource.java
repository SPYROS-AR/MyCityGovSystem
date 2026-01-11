package gr.hua.dit.mycitygov.web.rest;

import gr.hua.dit.mycitygov.core.security.ClientDetails;
import gr.hua.dit.mycitygov.core.security.ClientDetailsService;
import gr.hua.dit.mycitygov.core.security.JwtService;
import gr.hua.dit.mycitygov.web.rest.model.ClientTokenRequest;
import gr.hua.dit.mycitygov.web.rest.model.ClientTokenResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class ClientAuthResource {

    private final ClientDetailsService clientDetailsService;
    private final JwtService jwtService;

    public ClientAuthResource(ClientDetailsService clientDetailsService, JwtService jwtService) {
        this.clientDetailsService = clientDetailsService;
        this.jwtService = jwtService;
    }

    @PostMapping("/client-tokens")
    public ClientTokenResponse clientToken(@RequestBody @Valid ClientTokenRequest request) {
        // 1. Αυθεντικοποίηση του Client
        ClientDetails client = clientDetailsService.authenticate(request.clientId(), request.clientSecret())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        // 2. Έκδοση του Token με πρόθεμα "client:"
        String token = jwtService.issue("client:" + client.id(), client.roles());

        // 3. Επιστροφή της απάντησης (π.χ. διάρκεια 1 ώρα = 3600 δευτ.)
        return new ClientTokenResponse(token, "Bearer", 3600);
    }
}
