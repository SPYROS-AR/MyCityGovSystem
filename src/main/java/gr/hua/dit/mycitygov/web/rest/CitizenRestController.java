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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/citizen")
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

    // --- REQUESTS ---

    @GetMapping("/requests")
    public ResponseEntity<List<RequestView>> getMyRequests() {
        Long currentCitizenId = 3L;
        List<Request> requests = citizenService.getMyRequests(currentCitizenId);

        List<RequestView> requestViews = requests.stream()
                .map(requestMapper::convertRequestToRequestView)
                .collect(Collectors.toList());
        return ResponseEntity.ok(requestViews);
    }

    @PostMapping("/requests")
    public ResponseEntity<RequestView> createRequest(@RequestBody SubmitRequestRequest submitRequest) {
        Long currentCitizenId = 3L;
        Request savedRequest = citizenService.saveRequest(submitRequest, currentCitizenId);
        return ResponseEntity.ok(requestMapper.convertRequestToRequestView(savedRequest));
    }

    // --- APPOINTMENTS ---

    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentView>> getMyAppointments() {
        Long currentCitizenId = 3L;
        List<Appointment> appointments = citizenService.getMyAppointments(currentCitizenId);

        List<AppointmentView> appointmentViews = appointments.stream()
                .map(appointmentMapper::toAppointmentView)
                .collect(Collectors.toList());

        return ResponseEntity.ok(appointmentViews);
    }

    @PostMapping("/appointments")
    public ResponseEntity<AppointmentView> bookAppointment(@RequestBody BookAppointmentRequest bookRequest) {
        Long currentCitizenId = 3L;
        Appointment savedAppointment = citizenService.bookAppointment(bookRequest, currentCitizenId);
        return ResponseEntity.ok(appointmentMapper.toAppointmentView(savedAppointment));
    }
}