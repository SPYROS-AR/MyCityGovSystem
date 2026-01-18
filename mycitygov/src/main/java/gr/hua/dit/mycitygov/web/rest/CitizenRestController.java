package gr.hua.dit.mycitygov.web.rest;

import gr.hua.dit.mycitygov.core.model.Appointment;
import gr.hua.dit.mycitygov.core.model.Request;
import gr.hua.dit.mycitygov.core.repository.DepartmentScheduleRepository;
import gr.hua.dit.mycitygov.core.service.CitizenService;
import gr.hua.dit.mycitygov.core.service.mapper.AppointmentMapper;
import gr.hua.dit.mycitygov.core.service.mapper.DepartmentScheduleMapper;
import gr.hua.dit.mycitygov.core.service.mapper.RequestMapper;
import gr.hua.dit.mycitygov.core.service.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final DepartmentScheduleRepository scheduleRepository;
    private final DepartmentScheduleMapper scheduleMapper;

    public CitizenRestController(CitizenService citizenService,
                                 RequestMapper requestMapper,
                                 AppointmentMapper appointmentMapper, DepartmentScheduleRepository scheduleRepository, DepartmentScheduleMapper scheduleMapper) {
        this.citizenService = citizenService;
        this.requestMapper = requestMapper;
        this.appointmentMapper = appointmentMapper;
        this.scheduleRepository = scheduleRepository;
        this.scheduleMapper = scheduleMapper;
    }

    private Long getCurrentUserId(Authentication authentication) {
        return citizenService.getCitizenByUsername(authentication.getName()).getId();
    }

    // --- REQUESTS ---

    @GetMapping("/requests")
    public ResponseEntity<List<RequestView>> getMyRequests(Principal principal) {
        Long citizenId = citizenService.getCitizenByUsername(principal.getName()).getId();
        List<Request> requests = citizenService.getMyRequests(citizenId);
        return ResponseEntity.ok(requests.stream().map(requestMapper::toDto).collect(Collectors.toList()));
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
        return ResponseEntity.ok(appointments.stream().map(appointmentMapper::toAppointmentView).collect(Collectors.toList()));
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

    @GetMapping("/department/{id}/schedule")
    public ResponseEntity<List<DepartmentScheduleView>> getDepartmentSchedule(@PathVariable Long id) {
        var schedules = scheduleRepository.findByDepartmentId(id);
        var dtos = schedules.stream()
                .map(scheduleMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}