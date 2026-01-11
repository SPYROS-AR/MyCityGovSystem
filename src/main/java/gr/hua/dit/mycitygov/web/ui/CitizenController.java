package gr.hua.dit.mycitygov.web.ui;

import gr.hua.dit.mycitygov.core.model.Appointment;
import gr.hua.dit.mycitygov.core.model.Request;
import gr.hua.dit.mycitygov.core.service.CitizenService;
import gr.hua.dit.mycitygov.core.service.mapper.AppointmentMapper;
import gr.hua.dit.mycitygov.core.service.mapper.RequestMapper;
import gr.hua.dit.mycitygov.core.service.model.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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

    /**
     * Redirects to login page if accessing root /citizen.
     */
    @GetMapping
    public String index() {
        return "redirect:/citizen/login";
    }

    // --- AUTHENTICATION (LOGIN & REGISTER) ---

    @GetMapping("/login")
    public String showLoginForm() {
        return "citizen/login";
    }

    /**
     * Mock login handler - redirects to requests since Security is disabled.
     */
    @PostMapping("/login")
    public String login() {
        return "redirect:/citizen/requests/my";
    }

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
    public String submitRequest(@ModelAttribute SubmitRequestRequest submitRequest) {
        Long mockCitizenId = 3L; // TODO: Replace with Security ID
        citizenService.saveRequest(submitRequest, mockCitizenId);
        return "redirect:/citizen/requests/my";
    }

    @GetMapping("/requests/my")
    public String showMyRequests(Model model) {
        Long mockCitizenId = 3L;

        List<Request> requests = citizenService.getMyRequests(mockCitizenId);
        List<RequestView> requestViews = requests.stream()
                .map(requestMapper::convertRequestToRequestView)
                .collect(Collectors.toList());

        model.addAttribute("requests", requestViews);
        return "citizen/requests-list";
    }

    @GetMapping("/requests")
    public String redirectRequests() {
        return "redirect:/citizen/requests/my";
    }

    // --- APPOINTMENTS ---

    @GetMapping("/appointments/new")
    public String showBookAppointmentForm(Model model) {
        model.addAttribute("departments", citizenService.getAllDepartments());
        model.addAttribute("bookAppointment", new BookAppointmentRequest(null, null));
        return "citizen/appointment-new";
    }

    @PostMapping("/appointments")
    public String bookAppointment(@ModelAttribute BookAppointmentRequest bookAppointment) {
        Long mockCitizenId = 3L;
        citizenService.bookAppointment(bookAppointment, mockCitizenId);
        return "redirect:/citizen/appointments/my";
    }

    @GetMapping("/appointments/my")
    public String showMyAppointments(Model model) {
        Long mockCitizenId = 3L;

        List<Appointment> appointments = citizenService.getMyAppointments(mockCitizenId);
        List<AppointmentView> appointmentViews = appointments.stream()
                .map(appointmentMapper::toAppointmentView)
                .collect(Collectors.toList());

        model.addAttribute("appointments", appointmentViews);
        return "citizen/appointments-list";
    }

    @GetMapping("/appointments")
    public String redirectAppointments() {
        return "redirect:/citizen/appointments/my";
    }

    // --- PROFILE ---

    @GetMapping("/profile")
    public String showProfile() {
        return "citizen/profile";
    }
}