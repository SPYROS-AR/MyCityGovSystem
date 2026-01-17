package gr.hua.dit.mycitygov.web.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * UI controller for user authentication (login).
 */
@Controller
public class AuthController {

    @GetMapping("/login")
    public String login(
            final Authentication authentication,
            final HttpServletRequest request,
            final Model model
    ) {
        if (authentication != null && authentication.isAuthenticated() &&
                !authentication.getPrincipal().equals("anonymousUser")) {

            var authorities = authentication.getAuthorities();

            if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                return "redirect:/admin/dashboard";
            }
            else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYEE"))) {
                return "redirect:/employee/dashboard";
            }
            else {
                return "redirect:/citizen/requests";
            }
        }


        if (request.getParameter("error") != null) {
            model.addAttribute("error", "Invalid email or password.");
        }
        if (request.getParameter("logout") != null) {
            model.addAttribute("message", "You have been logged out.");
        }
        return "login";
    }
}