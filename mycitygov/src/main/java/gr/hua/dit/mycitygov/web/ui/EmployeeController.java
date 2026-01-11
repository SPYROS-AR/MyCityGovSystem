package gr.hua.dit.mycitygov.web.ui;

import gr.hua.dit.mycitygov.core.service.EmployeeService;
import gr.hua.dit.mycitygov.core.service.model.RequestView;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;

/**
 * Controller for handling all web requests related to the Employee
 * Manages the dashboard, request processing (approval/rejection), and appointment management
 */
@Controller
@PreAuthorize("hasRole('EMPLOYEE')")
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
     * @return The "employee/dashboard" view template (HTML page)
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        String username = principal.getName();  // username to search in the db
        Long empId = employeeService.getEmployeeByUsername(username).getId();    // get employee dto
        model.addAttribute("requests", employeeService.getRequestsForEmployeeDepartment(empId));
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
        RequestView request = employeeService.getRequestById(id);
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
    public String assignRequest(@PathVariable("id") Long requestId, Principal principal) {
        Long currentEmployeeId = employeeService.getEmployeeByUsername(principal.getName()).getId();
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
    public String approveRequest(@PathVariable("id") Long requestId, Principal principal) {
        Long currentEmployeeId = employeeService.getEmployeeByUsername(principal.getName()).getId();
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
                                @RequestParam(required = false) String reason,
                                Principal principal) {
        Long currentEmployeeId = employeeService.getEmployeeByUsername(principal.getName()).getId();

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
    public String appointments(Model model, Principal principal) {
        Long currentEmployeeId = employeeService.getEmployeeByUsername(principal.getName()).getId();
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
    public String confirmAppointment(@PathVariable("id") Long id,
                                     RedirectAttributes redirectAttributes) {
        employeeService.confirmAppointment(id);
        redirectAttributes.addFlashAttribute("successMessage", "Appointment confirmed successfully.");
        return "redirect:/employee/appointments";
    }

    /**
     * Cancels an appointment
     *
     * @param id The ID of the appointment
     * @return Redirects back to the appointments list
     */
    @PostMapping("/appointment/{id}/cancel")
    public String cancelAppointment(@PathVariable("id") Long id,
                                    RedirectAttributes redirectAttributes) {
        employeeService.cancelAppointment(id);
        redirectAttributes.addFlashAttribute("successMessage", "Appointment cancelled.");
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
                                        @RequestParam("rescheduledDate") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime rescheduledDate,
                                        RedirectAttributes redirectAttributes) {

        try {
            employeeService.rescheduleAppointment(id, rescheduledDate);
            redirectAttributes.addFlashAttribute("successMessage", "Appointment rescheduled successfully.");
        } catch (RuntimeException e) {
            // Catch the validation errors from the Service and send them to the view
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/employee/appointments";
    }
}