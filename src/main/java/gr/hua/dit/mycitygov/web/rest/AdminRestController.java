package gr.hua.dit.mycitygov.web.rest;

import gr.hua.dit.mycitygov.core.service.AdminService;
import gr.hua.dit.mycitygov.core.service.model.CreateRequestTypeRequest;
import gr.hua.dit.mycitygov.core.service.model.SystemStatistics;
import gr.hua.dit.mycitygov.core.service.model.UserView;
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

    //  Return all users (DTOs)
    @GetMapping("/users")
    public ResponseEntity<List<UserView>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
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

    // 5. Dashboard Stats
    @GetMapping("/stats")
    public ResponseEntity<SystemStatistics> getStats() {
        return ResponseEntity.ok(adminService.getSystemStatistics());
    }
}