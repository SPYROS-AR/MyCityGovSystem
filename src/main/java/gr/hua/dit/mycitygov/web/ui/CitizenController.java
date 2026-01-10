package gr.hua.dit.mycitygov.web.ui;

import gr.hua.dit.mycitygov.core.model.Appointment;
import gr.hua.dit.mycitygov.core.model.Request;
import gr.hua.dit.mycitygov.core.service.CitizenService;
import gr.hua.dit.mycitygov.core.service.mapper.AppointmentMapper;
import gr.hua.dit.mycitygov.core.service.mapper.RequestMapper;
import gr.hua.dit.mycitygov.core.service.model.AppointmentView;
import gr.hua.dit.mycitygov.core.service.model.BookAppointmentRequest;
import gr.hua.dit.mycitygov.core.service.model.RequestView;
import gr.hua.dit.mycitygov.core.service.model.SubmitRequestRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/citizen")
public class CitizenController {

    private final CitizenService citizenService;
    private final RequestMapper requestMapper;         // Προσθήκη
    private final AppointmentMapper appointmentMapper; // Προσθήκη

    public CitizenController(CitizenService citizenService,
                             RequestMapper requestMapper,
                             AppointmentMapper appointmentMapper) {
        this.citizenService = citizenService;
        this.requestMapper = requestMapper;
        this.appointmentMapper = appointmentMapper;
    }

    @GetMapping
    public String index() {
        return "redirect:/citizen/requests/my";
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
        Long mockCitizenId = 3L;
        citizenService.saveRequest(submitRequest, mockCitizenId);
        return "redirect:/citizen/requests/my";
    }

    @GetMapping("/requests/my")
    public String showMyRequests(Model model) {
        Long mockCitizenId = 3L;

        // 1. Παίρνουμε τα Entities
        List<Request> requests = citizenService.getMyRequests(mockCitizenId);

        // 2. Τα μετατρέπουμε σε DTOs (RequestView) για να ταιριάζουν με το HTML
        List<RequestView> requestViews = requests.stream()
                .map(requestMapper::convertRequestToRequestView)
                .collect(Collectors.toList());

        model.addAttribute("requests", requestViews);
        return "citizen/requests-list";
    }

    // Fix for 405 error
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

        // 1. Παίρνουμε τα Entities
        List<Appointment> appointments = citizenService.getMyAppointments(mockCitizenId);

        // 2. Τα μετατρέπουμε σε DTOs (AppointmentView)
        List<AppointmentView> appointmentViews = appointments.stream()
                .map(appointmentMapper::toAppointmentView)
                .collect(Collectors.toList());

        model.addAttribute("appointments", appointmentViews);
        return "citizen/appointments-list";
    }

    // Fix for 405 error
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