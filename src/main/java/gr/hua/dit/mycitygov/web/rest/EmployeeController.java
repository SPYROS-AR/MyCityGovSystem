package gr.hua.dit.mycitygov.web.rest;

import gr.hua.dit.mycitygov.core.model.Request;
import gr.hua.dit.mycitygov.core.service.EmployeeService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Controller for handling all web requests related to the Employee
 * Manages the dashboard, request processing (approval/rejection), and appointment management
 */
@Controller
@RequestMapping("/employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * Displays the Employee Dashboard
     * Fetches all requests assigned to the employee's department
     *
     * @param model UI Model to pass data to the view
     * @return The "employee/home" view template (HTML page)
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // TODO that would be done with Spring Security
        Long currentEmployeeId = 1L; // mock id
        model.addAttribute("requests", employeeService.getRequestsForEmployeeDepartment(currentEmployeeId));
        return "employee/dashboard";
    }

    /**
     * Displays the detailed view of a specific request
     *
     * @param id The ID of the request
     * @param model UI Model
     * @return The "employee/request_details" view template
     */
    @GetMapping("/request/{id}")
    public String viewRequestDetails(@PathVariable("id") Long id, Model model) {
        Request request = employeeService.getRequestById(id);
        model.addAttribute("request", request);
        return "employee/request_details";
    }


    /**
     * Assigns a specific request to the currently logged-in employee
     * Changes status from SUBMITTED to PROCESSING
     *
     * @param requestId The ID of the request
     * @return Redirects back to the dashboard
     */
    @PostMapping("/request/{id}/assign")
    public String assignRequest(@PathVariable("id") Long requestId) {
        Long currentEmployeeId = 1L; // Mock ID  (TODO: Security)
        employeeService.assignRequestToEmployee(requestId, currentEmployeeId);
        return "redirect:/employee/dashboard";
    }

    /**
     * Approves a request and marks it as COMPLETED
     * Triggers an email notification to the citizen
     *
     * @param requestId The ID of the request
     * @return Redirects back to the dashboard
     */
    @PostMapping("/request/{id}/approve")
    public String approveRequest(@PathVariable("id") Long requestId) {
        Long currentEmployeeId = 1L;
        employeeService.approveRequest(requestId, currentEmployeeId);
        return "redirect:/employee/dashboard";
    }

    /**
     * Rejects a request and marks it as REJECTED
     * Logs the reason for rejection and notifies the citizen via email
     *
     * @param requestId The ID of the request
     * @param reason The reason provided by the employee for the rejection
     * @return Redirects back to the dashboard
     */
    @PostMapping("/request/{id}/reject")
    public String rejectRequest(@PathVariable("id") Long requestId,
                                @RequestParam(required = false) String reason) {
        Long currentEmployeeId = 1L;

        // default rejection message
        if (reason == null || reason.trim().isEmpty()) {
            reason = "Request not found";
        }

        employeeService.rejectRequest(requestId, currentEmployeeId, reason);
        return "redirect:/employee/dashboard";
    }

    /**
     * Displays the list of appointments for the employee's department
     *
     * @param model UI Model
     * @return The "employee/appointments" view template
     */
    @GetMapping("/appointments")
    public String appointments(Model model) {
        Long currentEmployeeId = 1L;
        model.addAttribute("appointments", employeeService.getAppointmentsForEmployeeDepartment(currentEmployeeId));
        return "employee/appointments";
    }

    /**
     * Confirms an appointment
     *
     * @param id The ID of the appointment
     * @return Redirects back to the appointments list
     */
    @PostMapping("/appointment/{id}/confirm")
    public String confirmAppointment(@PathVariable("id") Long id) {
        employeeService.confirmAppointment(id);
        return "redirect:/employee/appointments";
    }

    /**
     * Cancels an appointment
     *
     * @param id The ID of the appointment
     * @return Redirects back to the appointments list
     */
    @PostMapping("/appointment/{id}/cancel")
    public String cancelAppointment(@PathVariable("id") Long id) {
        employeeService.cancelAppointment(id);
        return "redirect:/employee/appointments";
    }

    /**
     * Reschedules an appointment to a new date and time
     * Notifies the citizen about the change via email
     *
     * @param id The ID of the appointment
     * @param rescheduledDate The new date and time provided by the employee
     * @return Redirects back to the appointments list
     */
    @PostMapping("/appointment/{id}/reschedule")
    public String rescheduleAppointment(@PathVariable("id") Long id,
                                        @RequestParam("rescheduledDate") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime rescheduledDate) {
        employeeService.rescheduleAppointment(id, rescheduledDate);
        return "redirect:/employee/appointments";
    }
}