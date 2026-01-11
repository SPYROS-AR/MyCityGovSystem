package gr.hua.dit.mycitygov.web.rest;

import gr.hua.dit.mycitygov.core.model.Department;
import gr.hua.dit.mycitygov.core.service.AdminService;
import gr.hua.dit.mycitygov.core.service.model.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminRestController {

    private final AdminService adminService;


    public AdminRestController(AdminService adminService) {
        this.adminService = adminService;
    }

    // Dashboard stats
    @GetMapping("/stats")
    public ResponseEntity<SystemStatistics> getStats() {
        return ResponseEntity.ok(adminService.getSystemStatistics());
    }

    //  Return all users (DTOs)
    @GetMapping("/users")
    public ResponseEntity<List<UserView>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }


    // --- REQUEST TYPES LIST ---

    @GetMapping("/request-types")
    public ResponseEntity<List<RequestTypeView>> getAllRequestTypes() {
        return ResponseEntity.ok(adminService.getAllRequestTypes());
    }

    @PostMapping("/request-types/new")
    public ResponseEntity<?> createRequestType(
            @Valid @RequestBody CreateRequestTypeRequest request, // @RequestBody αντί για @ModelAttribute
            BindingResult bindingResult) {

        // Validation
        if (bindingResult.hasErrors()) {
            // If there are mistakes return 400 Bad Request as well as list of mistakes
            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            FieldError::getField,
                            FieldError::getDefaultMessage
                    ));
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            // Save
            adminService.createRequestType(request);
            // Return 200 OK
            return ResponseEntity.ok("Request Type created successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/request-types/{id}/reassign")
    public ResponseEntity<String> reassignRequestType(
            @PathVariable Long id,
            @RequestParam Long deptId) {

        try {
            adminService.reassignRequestType(id, deptId);
            return ResponseEntity.ok("Department updated successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping("/request-types/{id}/toggle")
    public ResponseEntity<String> toggleRequestType(@PathVariable Long id) {
        adminService.toggleRequestTypeStatus(id);
        return ResponseEntity.ok("Status toggled");
    }

    // --- DEPARTMENT MANAGEMENT ---

    // List all departments
    @GetMapping("/departments")
    public ResponseEntity<List<DepartmentView>> getAllDepartments() {
        return ResponseEntity.ok(adminService.getAllDepartments());
    }

    // Create New Department
    @PostMapping("/departments")
    public ResponseEntity<String> createDepartment(
            @RequestParam String name,
            @RequestParam String description) {

        try {
            adminService.createDepartment(name, description);
            return ResponseEntity.ok("Department created successfully");
        } catch (Exception e) {
            // Return 400 BAD REQUEST ERROR
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get Department Info & Schedule
    @GetMapping("/departments/{id}")
    public ResponseEntity<?> getDepartmentDetails(@PathVariable Long id) {
        try {
            // We can return a map or a composite DTO containing both info and schedule
            Department dept = adminService.getDepartmentById(id);
            List<DepartmentScheduleView> schedule = adminService.getDepartmentSchedule(id);

            Map<String, Object> response = Map.of(
                    "department", dept,
                    "schedule", schedule
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Update Department Info (name, description)
    @PutMapping("/departments/{id}")
    public ResponseEntity<String> updateDepartmentInfo(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String description) {
        try {
            adminService.updateDepartmentInfo(id, name, description);
            return ResponseEntity.ok("Department updated successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Update Department Schedule
    @PostMapping("/departments/{id}/schedule")
    public ResponseEntity<String> updateDepartmentSchedule(
            @PathVariable Long id,
            @RequestParam java.time.DayOfWeek day,
            @RequestParam java.time.LocalTime start,
            @RequestParam java.time.LocalTime end) {
        try {
            adminService.updateDepartmentSchedule(id, day, start, end);
            return ResponseEntity.ok("Schedule updated successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


}