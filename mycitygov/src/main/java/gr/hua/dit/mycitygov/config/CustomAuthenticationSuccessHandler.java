package gr.hua.dit.mycitygov.config;

import gr.hua.dit.mycitygov.core.model.Citizen;
import gr.hua.dit.mycitygov.core.port.SmsNotificationPort;
import gr.hua.dit.mycitygov.core.repository.CitizenRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final SmsNotificationPort smsPort;
    private final CitizenRepository citizenRepository;

    public CustomAuthenticationSuccessHandler(SmsNotificationPort smsPort, CitizenRepository citizenRepository) {
        this.smsPort = smsPort;
        this.citizenRepository = citizenRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String username = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();

            if (role.equals("ROLE_ADMIN") || role.equals("ADMIN")) { // Check for both just in case
                response.sendRedirect("/admin/dashboard");
                return;
            } else if (role.equals("ROLE_EMPLOYEE") || role.equals("EMPLOYEE")) {
                response.sendRedirect("/employee/dashboard");
                return;
            } else if (role.equals("ROLE_CITIZEN") || role.equals("CITIZEN")) {
                // --- SMS NOTIFICATION: LOGIN ---
                // Find citizen to get phone number
                Optional<Citizen> citizenOpt = citizenRepository.findByUsername(username);
                if (citizenOpt.isPresent()) {
                    String phone = citizenOpt.get().getMobilePhoneNumber();
                    // Send async or fire-and-forget (SmsPort is usually sync, but it's fast enough)
                    smsPort.sendSms(phone, "Security Alert: New login to your MyCityGov account.");
                }

                response.sendRedirect("/citizen/requests"); //
                return;
            }
        }

        // Default redirect
        response.sendRedirect("/");
    }
}
