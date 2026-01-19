package gr.hua.dit.mycitygov.web.ui;


import gr.hua.dit.mycitygov.core.model.Department;
import gr.hua.dit.mycitygov.core.service.AdminService;
import gr.hua.dit.mycitygov.core.service.model.CreateRequestTypeRequest;
import gr.hua.dit.mycitygov.core.service.model.DepartmentScheduleView;
import gr.hua.dit.mycitygov.core.service.model.SystemStatistics;
import gr.hua.dit.mycitygov.core.service.model.UserView;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

/**
 * Controller class responsible for handling administrative operations in the web UI.
 * <p>
 * This controller manages routes under the "/admin" path and provides functionality for:
 * <ul>
 * <li>Viewing the system dashboard and statistics.</li>
 * <li>Managing users (Citizens, Employees, Admins).</li>
 * <li>Managing Request Types (Services) including creation, toggling status, and reassignment.</li>
 * <li>Managing Departments, including creation, editing details, and setting weekly schedules.</li>
 * </ul>
 * </p>
 * Access is restricted to users with the 'ADMIN' role.
 */
@Controller
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // GENERAL

    /**
     * Redirects the root admin path ("/admin") directly to the dashboard.
     *
     * @return A redirect string to "/admin/dashboard".
     */
    @GetMapping // redirect straight to dashboard (from /admin)
    public String redirectAdmin() {
        return "redirect:/admin/dashboard";
    }

    /**
     * Displays the Admin Dashboard page containing system-wide statistics.
     *
     * @param model The UI Model used to pass statistics data to the view.
     * @return The "admin/dashboard" view template.
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        SystemStatistics stats = adminService.getSystemStatistics();
        model.addAttribute("stats", stats);
        return "admin/dashboard";
    }


    // -- USERS --

    /**
     * Displays a list of all registered users in the system.
     *
     * @param model The UI Model used to pass the list of users to the view.
     * @return The "admin/users" view template.
     */
    @GetMapping("/users")
    public String users(Model model) {
        List<UserView> users = adminService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }

    @GetMapping("/users/{id}/info")
    public String userDetails(@PathVariable Long id, Model model) {
        Object userDto = adminService.getUserDetails(id);

        model.addAttribute("userDetails", userDto);

        // We can use a simple logic to determine the type for the UI
        String userType = "ADMIN";
        if (userDto instanceof gr.hua.dit.mycitygov.core.service.model.CitizenView) {
            userType = "CITIZEN";
        } else if (userDto instanceof gr.hua.dit.mycitygov.core.service.model.EmployeeView) {
            userType = "EMPLOYEE";
        }
        model.addAttribute("userType", userType);

        return "admin/user_details";
    }


    // --- REQUEST TYPES MANAGEMENT ---

    /**
     * Displays a list of all available request types (services) and allows management actions.
     *
     * @param model The UI Model used to pass request types and departments to the view.
     * @return The "admin/request_types" view template.
     */
    @GetMapping("/request-types")
    public String listRequestTypes(Model model) {
        model.addAttribute("requestTypes", adminService.getAllRequestTypes());
        model.addAttribute("departments", adminService.getAllDepartments());
        return "admin/request_types";
    }

    /**
     * Displays the form for creating a new Request Type.
     *
     * @param model The UI Model used to initialize the form object and load departments for the dropdown.
     * @return The "admin/create_request_type" view template.
     */
    @GetMapping("/request-types/new")
    public String showCreateRequestTypeForm(Model model) {

        CreateRequestTypeRequest form = new CreateRequestTypeRequest("", null, 1, null, "");

        model.addAttribute("form", form);
        // We need the list of departments for the dropdown menu
        model.addAttribute("departments", adminService.getAllDepartments());

        return "admin/create_request_type";
    }

    /**
     * Handles the submission of the "Create Request Type" form.
     * <p>
     * Validates the input and, if successful, creates the new request type.
     * If validation fails, returns the user to the form with error messages.
     * </p>
     *
     * @param request       The DTO containing the form data.
     * @param bindingResult Captures validation errors.
     * @param model         The UI Model to reload data in case of errors.
     * @return A redirect to the list page on success, or the form view on error.
     */
    @PostMapping("/request-types/new")
    public String createRequestType(
            @Valid @ModelAttribute("form") CreateRequestTypeRequest request,
            BindingResult bindingResult,
            Model model) {

        // If there are validation errors (e.g. empty name), return to the form
        if (bindingResult.hasErrors()) {
            // Re-populate departments so the dropdown works
            model.addAttribute("departments", adminService.getAllDepartments());
            return "admin/create_request_type";
        }

        // Save the new request type via the service
        adminService.createRequestType(request);

        // Redirect to the list page on success
        return "redirect:/admin/request-types";
    }

    /**
     * Toggles the active status of a specific Request Type.
     *
     * @param id The ID of the request type to toggle.
     * @return A redirect back to the request types list.
     */
    @PostMapping("/request-types/{id}/toggle")
    public String toggleStatus(@PathVariable Long id) {
        adminService.toggleRequestTypeStatus(id);
        return "redirect:/admin/request-types";
    }

    /**
     * Reassigns a Request Type to a different Department.
     *
     * @param requestTypeId The ID of the request type to move.
     * @param departmentId  The ID of the new target department.
     * @return A redirect back to the request types list.
     */
    @PostMapping("/request-types/reassign")
    public String reassignDepartment(@RequestParam Long requestTypeId, @RequestParam Long departmentId) {
        adminService.reassignRequestType(requestTypeId, departmentId);
        return "redirect:/admin/request-types";
    }


    // --- DEPARTMENT MANAGEMENT ---

    /**
     * Displays a list of all departments.
     *
     * @param model The UI Model used to pass the list of departments to the view.
     * @return The "admin/departments" view template.
     */
    @GetMapping("/departments")
    public String listDepartments(Model model) {
        model.addAttribute("departments", adminService.getAllDepartments());
        return "admin/departments"; // You need to create this template if you want it to work
    }

    /**
     * Creates a new Department.
     *
     * @param name               The name of the new department.
     * @param description        The description of the new department.
     * @param redirectAttributes Used to pass success or error messages to the redirected page.
     * @return A redirect back to the departments list.
     */
    @PostMapping("/departments")
    public String createDepartment(@RequestParam("name") String name,
                                   @RequestParam("description") String description,
                                   RedirectAttributes redirectAttributes) {
        try {
            adminService.createDepartment(name, description);
            redirectAttributes.addFlashAttribute("successMessage", "Department created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }

        return "redirect:/admin/departments";
    }

    /**
     * Displays detailed information and schedule for a specific department.
     * <p>
     * This page allows editing basic info and managing the weekly opening hours.
     * </p>
     *
     * @param id    The ID of the department.
     * @param model The UI Model used to pass department details, schedule, and days of week.
     * @return The "admin/department-info" view template.
     */
    @GetMapping("/departments/{id}/info")
    public String departmentInfo(@PathVariable Long id, Model model) {
        // Get department
        Department department = adminService.getDepartmentById(id);
        // get departmnet schedule
        List<DepartmentScheduleView> schedule = adminService.getDepartmentSchedule(id);

        model.addAttribute("department", department);
        model.addAttribute("schedule", schedule);

        // dropdown
        model.addAttribute("days", java.time.DayOfWeek.values());

        return "admin/department-info";
    }

    /**
     * Updates the basic information (name and description) of a department.
     *
     * @param id          The ID of the department to update.
     * @param name        The new name.
     * @param description The new description.
     * Adds appropriate message to model
     * @return A redirect back to the department info page.
     */
    @PostMapping("/departments/{id}/update")
    public String updateDepartmentInfo(@PathVariable Long id,
                                       @RequestParam("name") String name,
                                       @RequestParam("description") String description,
                                       RedirectAttributes redirectAttributes) { // 1. Προσθήκη παραμέτρου
        try {
            adminService.updateDepartmentInfo(id, name, description);
            // Success message
            redirectAttributes.addFlashAttribute("successMessage", "Department updated successfully!");
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // Catch "Duplicate Key" error
            redirectAttributes.addFlashAttribute("errorMessage", "Error: A department with this name already exists.");
        } catch (Exception e) {
            // Catch general error
            redirectAttributes.addFlashAttribute("errorMessage", "Update failed: " + e.getMessage());
        }
        return "redirect:/admin/departments/" + id + "/info";
    }

    /**
     * Updates or creates a schedule entry for a specific day for a department.
     * <p>
     * If the service throws an error (e.g. start time after end time), the error message
     * is captured and displayed to the user via Flash Attributes.
     * </p>
     *
     * @param id                 The ID of the department.
     * @param day                The day of the week to set hours for.
     * @param start              The opening time.
     * @param end                The closing time.
     * @param redirectAttributes Used to pass success or error messages.
     * @return A redirect back to the department info page.
     */
    @PostMapping("/departments/{id}/schedule")
    public String updateSchedule(@PathVariable Long id,
                                @RequestParam DayOfWeek day,
                                @RequestParam LocalTime start,
                                @RequestParam LocalTime end,
                                RedirectAttributes redirectAttributes) {

        try {
            adminService.updateDepartmentSchedule(id, day, start, end);
            redirectAttributes.addFlashAttribute("successMessage", "Schedule updated successfully!");
        } catch (IllegalArgumentException e) {
            // We catch the error message from the Service and pass it to the UI
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());

            // Redirect back to the edit page so the user can fix the input
            return "redirect:/admin/departments/" + id + "/info";
        }

        return "redirect:/admin/departments/" + id + "/info";
    }
}
