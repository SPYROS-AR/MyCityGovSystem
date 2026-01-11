package gr.hua.dit.mycitygov.web.ui;

import gr.hua.dit.mycitygov.core.model.Appointment;
import gr.hua.dit.mycitygov.core.model.Request;
import gr.hua.dit.mycitygov.core.security.ApplicationUserDetails;
import gr.hua.dit.mycitygov.core.service.CitizenService;
import gr.hua.dit.mycitygov.core.service.mapper.AppointmentMapper;
import gr.hua.dit.mycitygov.core.service.mapper.RequestMapper;
import gr.hua.dit.mycitygov.core.service.model.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MVC Controller handling the Citizen's UI (HTML pages).
 */
@Controller
@RequestMapping("/citizen")
public class CitizenController {

    private final CitizenService citizenService;
    private final RequestMapper requestMapper;
    private final AppointmentMapper appointmentMapper;

    public CitizenController(CitizenService citizenService,
                             RequestMapper requestMapper,
                             AppointmentMapper appointmentMapper) {
        this.citizenService = citizenService;
        this.requestMapper = requestMapper;
        this.appointmentMapper = appointmentMapper;
    }

    private Long getCurrentUserId(Authentication authentication) {
        ApplicationUserDetails userDetails = (ApplicationUserDetails) authentication.getPrincipal();
        return userDetails.personId();
    }

    /**
     * Redirects to login page if accessing root /citizen.
     */
    @GetMapping
    public String index() {
        return "redirect:/citizen/login";
    }

    /**
     * Shows the login form.
     * @return The "citizen/login" view template.
     */
    @GetMapping("/login")
    public String showLoginForm() { return "citizen/login"; }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        // Empty DTO for the form
        model.addAttribute("citizen", new CreateCitizenRequest(
                "", "", "", "", "", "", "", ""
        ));
        return "citizen/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("citizen") CreateCitizenRequest request,
                           BindingResult bindingResult,
                           Model model) {
        // Call service to create citizen
        CreateCitizenResult result = citizenService.createCitizen(request);

        if (!result.created()) {
            // If error (e.g. email exists), show it on the page
            model.addAttribute("error", result.reason());
            model.addAttribute("citizen", request);
            return "citizen/register";
        }

        // Success -> Redirect to login
        return "redirect:/citizen/login?success";
    }

    // --- REQUESTS ---

    @GetMapping("/requests/new")
    public String showCreateRequestForm(Model model) {
        model.addAttribute("requestTypes", citizenService.getAllRequestTypes());
        model.addAttribute("submitRequest", new SubmitRequestRequest(null, ""));
        return "citizen/request-new";
    }

    @PostMapping("/requests")
    public String submitRequest(@ModelAttribute SubmitRequestRequest submitRequest, Principal principal) {
        Long citizenId = citizenService.getCitizenByUsername(principal.getName()).getId();
        citizenService.saveRequest(submitRequest, citizenId);
        return "redirect:/citizen/requests/my";
    }

    @GetMapping("/requests/my")
    public String showMyRequests(Model model, Principal principal) {
        var citizen = citizenService.getCitizenByUsername(principal.getName());

        if (citizen == null) {
            throw new RuntimeException("Logged in user is not found in Citizen database!");
        }

        Long citizenId = citizen.getId();

        List<Request> requests = citizenService.getMyRequests(citizenId);
        List<RequestView> requestViews = requests.stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());

        model.addAttribute("requests", requestViews);
        return "citizen/requests-list";
    }

    @GetMapping("/requests")
    public String redirectRequests() { return "redirect:/citizen/requests/my"; }

    // --- APPOINTMENTS ---

    @GetMapping("/appointments/new")
    public String showBookAppointmentForm(Model model) {
        model.addAttribute("departments", citizenService.getAllDepartments());
        model.addAttribute("bookAppointment", new BookAppointmentRequest(null, null));
        return "citizen/appointment-new";
    }

    @PostMapping("/appointments")
    public String bookAppointment(@ModelAttribute BookAppointmentRequest bookAppointment, Model model, Principal principal) {
        Long citizenId = citizenService.getCitizenByUsername(principal.getName()).getId();

        try {
            citizenService.bookAppointment(bookAppointment, citizenId);
            return "redirect:/citizen/appointments/my";
        } catch (RuntimeException e) {
            // Show error if appointment is outside working hours
            model.addAttribute("error", e.getMessage());
            model.addAttribute("departments", citizenService.getAllDepartments());
            return "citizen/appointment-new";
        }
    }

    @GetMapping("/appointments/my")
    public String showMyAppointments(Model model, Principal principal) {
        Long citizenId = citizenService.getCitizenByUsername(principal.getName()).getId();

        List<Appointment> appointments = citizenService.getMyAppointments(citizenId);
        List<AppointmentView> appointmentViews = appointments.stream()
                .map(appointmentMapper::toAppointmentView)
                .collect(Collectors.toList());

        model.addAttribute("appointments", appointmentViews);
        return "citizen/appointments-list";
    }

    @GetMapping("/appointments")
    public String redirectAppointments() { return "redirect:/citizen/appointments/my"; }

    // --- PROFILE ---

    @GetMapping("/profile")
    public String showProfile() { return "citizen/profile"; }
}