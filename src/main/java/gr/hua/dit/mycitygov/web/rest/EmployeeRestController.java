package gr.hua.dit.mycitygov.web.rest;

import gr.hua.dit.mycitygov.core.model.Appointment;
import gr.hua.dit.mycitygov.core.model.Request;
import gr.hua.dit.mycitygov.core.service.EmployeeService;
import gr.hua.dit.mycitygov.core.service.mapper.AppointmentMapper;
import gr.hua.dit.mycitygov.core.service.mapper.RequestMapper;
import gr.hua.dit.mycitygov.core.service.model.AppointmentView;
import gr.hua.dit.mycitygov.core.service.model.RequestView;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employee")
public class EmployeeRestController {

    private final EmployeeService employeeService;
    private final RequestMapper requestMapper;
    private final AppointmentMapper appointmentMapper;

    public EmployeeRestController(EmployeeService employeeService,
                                  RequestMapper requestMapper,
                                  AppointmentMapper appointmentMapper) {
        this.employeeService = employeeService;
        this.requestMapper = requestMapper;
        this.appointmentMapper = appointmentMapper;
    }

    @GetMapping("/requests")
    public ResponseEntity<List<RequestView>> getDepartmentRequests() {
        Long currentEmployeeId = 1L; // Mock ID
        List<Request> requests = employeeService.getRequestsForEmployeeDepartment(currentEmployeeId);

        // map to dto
        List<RequestView> requestViews = requests
                .stream()
                .map(requestMapper::convertRequestToRequestView)
                .collect(Collectors.toList());
        return ResponseEntity.ok(requestViews);
    }

    @GetMapping("/request/{id}")
    public ResponseEntity<RequestView> getRequest(@PathVariable Long id) {
        Request request = employeeService.getRequestById(id);
        return ResponseEntity.ok(requestMapper.convertRequestToRequestView(request));
    }

    @PostMapping("/request/{id}/assign")
    public ResponseEntity<RequestView> assignRequest(@PathVariable Long id) {
        Long currentEmployeeId = 1L;
        employeeService.assignRequestToEmployee(id, currentEmployeeId);
        return ResponseEntity.ok(requestMapper.
                convertRequestToRequestView(employeeService.
                        getRequestById(id)));
    }

    @PostMapping("/request/{id}/approve")
    public ResponseEntity<RequestView> approveRequest(@PathVariable Long id) {
        Long currentEmployeeId = 1L;
        employeeService.approveRequest(id, currentEmployeeId);
        return ResponseEntity.ok(requestMapper.convertRequestToRequestView(employeeService.getRequestById(id)));
    }

    @PostMapping("/request/{id}/reject")
    public ResponseEntity<RequestView> rejectRequest(@PathVariable Long id, @RequestBody String reason) {
        Long currentEmployeeId = 1L;
        employeeService.rejectRequest(id, currentEmployeeId, reason);
        return ResponseEntity.ok(requestMapper.convertRequestToRequestView(employeeService.getRequestById(id)));
    }

    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentView>> getAppointments() {
        Long currentEmployeeId = 1L;
        List<Appointment> entities = employeeService.getAppointmentsForEmployeeDepartment(currentEmployeeId);

        List<AppointmentView> appointmentViews = entities.stream()
                .map(appointmentMapper::toAppointmentView)
                .collect(Collectors.toList());

        return ResponseEntity.ok(appointmentViews);
    }

    @PostMapping("/appointment/{id}/confirm")
    public ResponseEntity<String> confirmAppointment(@PathVariable Long id) {
        employeeService.confirmAppointment(id);
        return ResponseEntity.ok("Appointment confirmed");
    }

    @PostMapping("/appointment/{id}/cancel")
    public ResponseEntity<String> cancelAppointment(@PathVariable Long id) {
        employeeService.cancelAppointment(id);
        return ResponseEntity.ok("Appointment cancelled");
    }

    @PostMapping("/appointment/{id}/reschedule")
    public ResponseEntity<String> rescheduleAppointment(
            @PathVariable Long id,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newDate) {

        employeeService.rescheduleAppointment(id, newDate);
        return ResponseEntity.ok("Appointment rescheduled to " + newDate);
    }

}
