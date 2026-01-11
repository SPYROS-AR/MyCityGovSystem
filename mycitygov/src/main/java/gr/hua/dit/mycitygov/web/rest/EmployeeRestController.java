package gr.hua.dit.mycitygov.web.rest;

import gr.hua.dit.mycitygov.core.service.EmployeeService;
import gr.hua.dit.mycitygov.core.service.model.AppointmentView;
import gr.hua.dit.mycitygov.core.service.model.RequestView;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * REST Controller for Employee Operations
 * <p>
 * This controller exposes endpoints for managing Requests and Appointments
 * related to the department of the logged-in employee
 * It provides functionality to list, assign, approve, reject requests,
 * and manage appointment schedules
 * </p>
 */
@RestController
@RequestMapping("/api/employee")
public class EmployeeRestController {

    private final EmployeeService employeeService;

    public EmployeeRestController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }


    @GetMapping // redirect straight to dashboard (from /employee)
    public String redirectEmployee() {
        return "redirect:/employee/dashboard";
    }

    /**
     * Retrieves all requests assigned to the current employee's department
     *
     * @return A list of {@link RequestView} DTOs representing the requests
     */
    @GetMapping("/requests")
    public ResponseEntity<List<RequestView>> getDepartmentRequests(Principal principal) {
        Long currentEmployeeId = employeeService.getEmployeeByUsername(principal.getName()).getId();
        List<RequestView> requests = employeeService.getRequestsForEmployeeDepartment(currentEmployeeId);
        return ResponseEntity.ok(requests);
    }

    /**
     * Retrieves the details of a specific request by its ID
     *
     * @param id The ID of the request to retrieve
     * @return The {@link RequestView} DTO of the request
     */
    @GetMapping("/request/{id}")
    public ResponseEntity<RequestView> getRequest(@PathVariable Long id) {
        RequestView request = employeeService.getRequestById(id);
        return ResponseEntity.ok((request));
    }

    /**
     * Assigns a specific request to the current employee
     *
     * @param id The ID of the request to assign
     * @return The updated {@link RequestView}
     */
    @PostMapping("/request/{id}/assign")
    public ResponseEntity<RequestView> assignRequest(@PathVariable Long id, Principal principal) {
        Long currentEmployeeId = employeeService.getEmployeeByUsername(principal.getName()).getId();
        employeeService.assignRequestToEmployee(id, currentEmployeeId);
        return ResponseEntity.ok(employeeService.getRequestById(id));
    }

    /**
     * Approves a request and marks it as COMPLETED
     *
     * @param id The ID of the request to approve
     * @return The updated {@link RequestView}
     */
    @PostMapping("/request/{id}/approve")
    public ResponseEntity<RequestView> approveRequest(@PathVariable Long id,  Principal principal) {
        Long currentEmployeeId = employeeService.getEmployeeByUsername(principal.getName()).getId();
        employeeService.approveRequest(id, currentEmployeeId);
        return ResponseEntity.ok(employeeService.getRequestById(id));
    }

    /**
     * Rejects a request with a reason and marks it as REJECTED
     *
     * @param id     The ID of the request to reject
     * @param reason The reason for rejection (plain text)
     * @return The updated {@link RequestView}
     */
    @PostMapping("/request/{id}/reject")
    public ResponseEntity<RequestView> rejectRequest(@PathVariable Long id,
                                                     @RequestBody String reason,
                                                     Principal principal) {
        Long currentEmployeeId = employeeService.getEmployeeByUsername(principal.getName()).getId();
        employeeService.rejectRequest(id, currentEmployeeId, reason);
        return ResponseEntity.ok(employeeService.getRequestById(id));
    }

    /**
     * Retrieves all appointments for the current employee's department
     *
     * @return A list of {@link AppointmentView} DTOs
     */
    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentView>> getAppointments(Principal principal) {
        Long currentEmployeeId = employeeService.getEmployeeByUsername(principal.getName()).getId();
        List<AppointmentView> appointmentViews = employeeService.getAppointmentsForEmployeeDepartment(currentEmployeeId);
        return ResponseEntity.ok(appointmentViews);
    }

    /**
     * Confirms an appointment
     *
     * @param id The ID of the appointment
     * @return A confirmation message
     */
    @PostMapping("/appointment/{id}/confirm")
    public ResponseEntity<String> confirmAppointment(@PathVariable Long id) {
        employeeService.confirmAppointment(id);
        return ResponseEntity.ok("Appointment confirmed");
    }

    /**
     * Cancels an appointment
     *
     * @param id The ID of the appointment
     * @return A cancellation message
     */
    @PostMapping("/appointment/{id}/cancel")
    public ResponseEntity<String> cancelAppointment(@PathVariable Long id) {
        employeeService.cancelAppointment(id);
        return ResponseEntity.ok("Appointment cancelled");
    }

    /**
     * Reschedules an appointment to a new date/time
     *
     * @param id      The ID of the appointment
     * @param newDate The new date and time (ISO format)
     * @return A success message with the new date
     */
    @PostMapping("/appointment/{id}/reschedule")
    public ResponseEntity<String> rescheduleAppointment(
            @PathVariable Long id,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newDate) {

        employeeService.rescheduleAppointment(id, newDate);
        return ResponseEntity.ok("Appointment rescheduled to " + newDate);
    }

}
