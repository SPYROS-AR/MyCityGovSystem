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

@Controller
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // GENERAL

    @GetMapping // redirect straight to dashboard (from /admin)
    public String redirectAdmin() {
        return "redirect:/admin/dashboard";
    }

    /**
     * Dashboard page with system statistics.
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        SystemStatistics stats = adminService.getSystemStatistics();
        model.addAttribute("stats", stats);
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String users(Model model) {
        List<UserView> users = adminService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }


    // --- REQUEST TYPES MANAGEMENT ---

    /**
     * List all request types.
     */
    @GetMapping("/request-types")
    public String listRequestTypes(Model model) {
        model.addAttribute("requestTypes", adminService.getAllRequestTypes());
        model.addAttribute("departments", adminService.getAllDepartments());
        return "admin/request_types";
    }

    /**
     * Show form to create a new request type.
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
     * Handle the submission of the new request type form.
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

    @PostMapping("/request-types/{id}/toggle")
    public String toggleStatus(@PathVariable Long id) {
        adminService.toggleRequestTypeStatus(id);
        return "redirect:/admin/request-types";
    }

    @PostMapping("/request-types/reassign")
    public String reassignDepartment(@RequestParam Long requestTypeId, @RequestParam Long departmentId) {
        adminService.reassignRequestType(requestTypeId, departmentId);
        return "redirect:/admin/request-types";
    }


    // --- DEPARTMENT MANAGEMENT ---

    /**
     * List all departments (Optional - requested by the dashboard link).
     */
    @GetMapping("/departments")
    public String listDepartments(Model model) {
        model.addAttribute("departments", adminService.getAllDepartments());
        return "admin/departments"; // You need to create this template if you want it to work
    }

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

    // Information and edit page
    @GetMapping("/departments/{id}/info")
    public String departmentInfo(@PathVariable Long id, Model model) {
        // Get department
        Department department = adminService.getDepartmentById(id);
        // get its schedule
        List<DepartmentScheduleView> schedule = adminService.getDepartmentSchedule(id);

        model.addAttribute("department", department);
        model.addAttribute("schedule", schedule);

        // dropdown
        model.addAttribute("days", java.time.DayOfWeek.values());

        return "admin/department-info"; // Το νέο template
    }

    // Update basic info (name, description)
    @PostMapping("/departments/{id}/update")
    public String updateDepartmentInfo(@PathVariable Long id,
                                       @RequestParam("name") String name,
                                       @RequestParam("description") String description) {
        adminService.updateDepartmentInfo(id, name, description);
        return "redirect:/admin/departments/" + id + "/info?success";
    }

    // Update schedule for specific department
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
