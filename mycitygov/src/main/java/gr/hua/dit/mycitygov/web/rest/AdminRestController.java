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


/**
 * REST controller providing administrative management endpoints.
 * <p>
 * This controller exposes a RESTful API restricted to users with the 'ADMIN' role.
 * It facilitates the monitoring of system statistics, management of users,
 * request types (services), and the organizational structure (departments) of the municipality.
 * </p>
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminRestController {

    private final AdminService adminService;

    public AdminRestController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * Retrieves system-wide statistics for the dashboard.
     * <p>
     * Returns data such as total requests, pending requests, requests per department, etc.
     * </p>
     *
     * @return A {@link ResponseEntity} containing the {@link SystemStatistics} DTO and HTTP 200 OK.
     */
    @GetMapping("/stats")
    public ResponseEntity<SystemStatistics> getStats() {
        return ResponseEntity.ok(adminService.getSystemStatistics());
    }

    // -- USERS --
    /**
     * Retrieves a list of all users registered in the system.
     *
     * @return A {@link ResponseEntity} containing a list of {@link UserView} DTOs and HTTP 200 OK.
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserView>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    /**
     * Returns DTO based on user's role
     *
     * @param id
     * @return
     */
    @GetMapping("/users/{id}/info")
    public ResponseEntity<?> getUserInformation(@PathVariable Long id) {
        try {
            // Get DTO based on role
            Object userDto = adminService.getUserDetails(id);
            return ResponseEntity.ok(userDto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    // --- REQUEST TYPES LIST ---

    /**
     * Retrieves all available request types (services).
     *
     * @return A {@link ResponseEntity} containing a list of {@link RequestTypeView} DTOs and HTTP 200 OK.
     */
    @GetMapping("/request-types")
    public ResponseEntity<List<RequestTypeView>> getAllRequestTypes() {
        return ResponseEntity.ok(adminService.getAllRequestTypes());
    }

    /**
     * Creates a new Request Type (Service).
     * <p>
     * Accepts a JSON body with the request type details. If validation fails,
     * returns HTTP 400 Bad Request with a map of field errors.
     * </p>
     *
     * @param request       The {@link CreateRequestTypeRequest} DTO containing the name, category, SLA, etc.
     * @param bindingResult Captures validation errors in the request body.
     * @return HTTP 200 OK with a success message, HTTP 400 Bad Request with validation errors,
     * or HTTP 500 Internal Server Error if creation fails.
     */
    @PostMapping("/request-types/new")
    public ResponseEntity<?> createRequestType(
            @Valid @RequestBody CreateRequestTypeRequest request,
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

    /**
     * Reassigns an existing Request Type to a different Department.
     *
     * @param id     The ID of the Request Type to reassign (from path).
     * @param deptId The ID of the new Department (query parameter).
     * @return HTTP 200 OK if successful, or HTTP 404 Not Found if the ID is invalid.
     */
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

    /**
     * Toggles the active status of a Request Type.
     *
     * @param id The ID of the Request Type to toggle.
     * @return HTTP 200 OK with a status message.
     */
    @PostMapping("/request-types/{id}/toggle")
    public ResponseEntity<String> toggleRequestType(@PathVariable Long id) {
        adminService.toggleRequestTypeStatus(id);
        return ResponseEntity.ok("Status toggled");
    }

    // --- DEPARTMENT MANAGEMENT ---

    /**
     * Retrieves a list of all departments.
     *
     * @return A {@link ResponseEntity} containing a list of {@link DepartmentView} DTOs and HTTP 200 OK.
     */
    @GetMapping("/departments")
    public ResponseEntity<List<DepartmentView>> getAllDepartments() {
        return ResponseEntity.ok(adminService.getAllDepartments());
    }

    /**
     * Creates a new Department.
     *
     * @param name        The name of the new department (query param).
     * @param description A description of the department (query param).
     * @return HTTP 200 OK if successful, or HTTP 400 Bad Request if the department name already exists.
     */
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

    /**
     * Retrieves detailed information about a specific department.
     * <p>
     * Returns a JSON object containing both the basic department info (entity)
     * and its weekly schedule list.
     * </p>
     *
     * @param id The ID of the department.
     * @return HTTP 200 OK with the details map, or HTTP 404 Not Found if the department does not exist.
     */
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

    /**
     * Updates the basic information (name and description) of a department.
     *
     * @param id          The ID of the department to update.
     * @param name        The new name.
     * @param description The new description.
     * @return HTTP 200 OK if successful, or HTTP 404 Not Found if the department ID is invalid.
     */
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

    /**
     * Updates or sets the schedule for a specific day for a department.
     *
     * @param id    The ID of the department.
     * @param day   The day of the week (e.g., MONDAY).
     * @param start The opening time (HH:mm).
     * @param end   The closing time (HH:mm).
     * @return HTTP 200 OK if successful, HTTP 400 Bad Request if times are invalid (start > end),
     * or HTTP 404 Not Found if the department does not exist.
     */
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