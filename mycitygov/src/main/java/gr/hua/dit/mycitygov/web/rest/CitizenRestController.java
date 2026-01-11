package gr.hua.dit.mycitygov.web.rest;

import gr.hua.dit.mycitygov.core.model.Appointment;
import gr.hua.dit.mycitygov.core.model.Request;
import gr.hua.dit.mycitygov.core.service.CitizenService;
import gr.hua.dit.mycitygov.core.service.mapper.AppointmentMapper;
import gr.hua.dit.mycitygov.core.service.mapper.RequestMapper;
import gr.hua.dit.mycitygov.core.service.model.AppointmentView;
import gr.hua.dit.mycitygov.core.service.model.BookAppointmentRequest;
import gr.hua.dit.mycitygov.core.service.model.RequestView;
import gr.hua.dit.mycitygov.core.service.model.SubmitRequestRequest;
import org.springframework.http.ResponseEntity;
<<<<<<< HEAD
import org.springframework.security.core.Authentication;
=======
import org.springframework.security.access.prepost.PreAuthorize;
>>>>>>> f67f8ad8fbdafd7359f62a4509f2f5293437490e
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/citizen")
@PreAuthorize("hasRole('CITIZEN')")
public class CitizenRestController {

    private final CitizenService citizenService;
    private final RequestMapper requestMapper;
    private final AppointmentMapper appointmentMapper;

    public CitizenRestController(CitizenService citizenService,
                                 RequestMapper requestMapper,
                                 AppointmentMapper appointmentMapper) {
        this.citizenService = citizenService;
        this.requestMapper = requestMapper;
        this.appointmentMapper = appointmentMapper;
    }

    private Long getCurrentUserId(Authentication authentication) {
        return citizenService.getCitizenByUsername(authentication.getName()).getId();
    }

    // --- REQUESTS ---

    @GetMapping("/requests")
    public ResponseEntity<List<RequestView>> getMyRequests(Principal principal) {
        Long citizenId = citizenService.getCitizenByUsername(principal.getName()).getId();
        List<Request> requests = citizenService.getMyRequests(citizenId);

        List<RequestView> requestViews = requests.stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(requestViews);
    }

    @PostMapping("/requests")
    public ResponseEntity<RequestView> createRequest(@RequestBody SubmitRequestRequest submitRequest, Principal principal) {
        Long citizenId = citizenService.getCitizenByUsername(principal.getName()).getId();
        Request savedRequest = citizenService.saveRequest(submitRequest, citizenId);

        return ResponseEntity.ok(requestMapper.toDto(savedRequest));
    }

    // --- APPOINTMENTS ---

    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentView>> getMyAppointments(Principal principal) {
        Long citizenId = citizenService.getCitizenByUsername(principal.getName()).getId();
        List<Appointment> appointments = citizenService.getMyAppointments(citizenId);

        List<AppointmentView> appointmentViews = appointments.stream()
                .map(appointmentMapper::toAppointmentView)
                .collect(Collectors.toList());

        return ResponseEntity.ok(appointmentViews);
    }

    @PostMapping("/appointments")
    public ResponseEntity<?> bookAppointment(@RequestBody BookAppointmentRequest bookRequest, Principal principal) {
        Long citizenId = citizenService.getCitizenByUsername(principal.getName()).getId();
        try {
            Appointment savedAppointment = citizenService.bookAppointment(bookRequest, citizenId);
            return ResponseEntity.ok(appointmentMapper.toAppointmentView(savedAppointment));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}