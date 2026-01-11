package gr.hua.dit.mycitygov.web.rest;

import gr.hua.dit.mycitygov.core.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for handling authentication and JWT issuance.
 * Provides endpoints for users to log in and receive security tokens.
 */

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthRestController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /**
     * Authenticates a user and issues a JWT token if successful.
     *  @param request A {@link LoginRequest} containing username and password.
     * @return A {@link ResponseEntity} containing the JWT string.
     * @throws org.springframework.security.core.AuthenticationException if credentials are invalid.
     */

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // Authenticate user with Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        // Get roles if login successful
        UserDetails user = (UserDetails) authentication.getPrincipal();
        List<String> roles = user.getAuthorities().stream()
                .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                .collect(Collectors.toList());

        // Create token
        String token = jwtService.issue(user.getUsername(), roles);

        return ResponseEntity.ok(new JwtResponse(token));
    }

    public record LoginRequest(String username, String password) {}
    public record JwtResponse(String token) {}
}
