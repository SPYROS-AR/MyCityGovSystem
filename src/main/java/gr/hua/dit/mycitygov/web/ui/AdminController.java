package gr.hua.dit.mycitygov.web.ui;


import gr.hua.dit.mycitygov.core.model.User;
import gr.hua.dit.mycitygov.core.service.AdminService;
import gr.hua.dit.mycitygov.core.service.model.CreateRequestTypeRequest;
import gr.hua.dit.mycitygov.core.service.model.SystemStatistics;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
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
    public String users(Model model){
        List<User> users = adminService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }

    /**
     * List all request types.
     */
    @GetMapping("/request-types")
    public String listRequestTypes(Model model) {
        model.addAttribute("requestTypes", adminService.getAllRequestTypes());
        return "admin/request_types";
    }

    /**
     * Show form to create a new request type.
     */
    @GetMapping("/request-types/new")
    public String showCreateRequestTypeForm(Model model) {
        // Initialize an empty DTO for the form binding
        // Assuming your record constructor matches: name, category, slaDays, departmentId, requiredDocuments
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

    /**
     * List all departments (Optional - requested by the dashboard link).
     */
    @GetMapping("/departments")
    public String listDepartments(Model model) {
        model.addAttribute("departments", adminService.getAllDepartments());
        return "admin/departments"; // You need to create this template if you want it to work
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
}
