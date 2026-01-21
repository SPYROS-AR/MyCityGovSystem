package gr.hua.dit.mycitygov.core.service;

import gr.hua.dit.mycitygov.core.model.Employee;
import gr.hua.dit.mycitygov.core.model.Request;
import gr.hua.dit.mycitygov.core.model.RequestDocument;
import gr.hua.dit.mycitygov.core.service.model.AppointmentView;
import gr.hua.dit.mycitygov.core.service.model.DepartmentScheduleView;
import gr.hua.dit.mycitygov.core.service.model.EmployeeView;
import gr.hua.dit.mycitygov.core.service.model.RequestView;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service interface defining the business logic for Employee operations
 */
public interface EmployeeService {
    /**
     * Retrieves all employees in the system
     * @return List of Employee entities
     */
    List<EmployeeView> getAllEmployees();

    /**
     * Retrieves all requests associated with the department of a specific employee
     * @param employeeId The ID of the employee.
     * @return List of RequestViews.
     */
    List<RequestView> getRequestsForEmployeeDepartment(Long employeeId);

    /**
     * Retrieves requests for a specific department
     * @param departmentId The ID of the department
     * @return List of RequestViews.
     */
    List<RequestView> getRequestsForDepartment(Long departmentId);

    /**
     * Retrieves all employees from a department
     * @param departmentId the department id
     * @return List of Employees
     */
    List<EmployeeView> getEmployeesByDepartment(Long departmentId);

    /**
     * Updates the status of a request and adds comment in the log
     * @param requestId the id of the request
     * @param employeeId the id of the employee
     * @param newStatus the new request status
     * @param comment the comment to be added
     */
    void updateRequestProgress(Long requestId, Long employeeId, Request.Status newStatus, String comment);

    /**
     * Retrieves a single request by its ID
     * @param id The request ID
     * @return The RequestView entity
     */
    @Transactional(readOnly = true)
    RequestView getRequestById(Long id);

    /**
     * Updates the status of a request directly
     * @param requestId The request ID
     * @param newStatus The new status to set
     */
    void updateRequestStatus(Long requestId, Request.Status newStatus);

    /**
     * Assigns a request to an employee and updates status to PROCESSING
     * @param requestId The request ID
     * @param employeeId The employee ID
     */
    void assignRequestToEmployee(Long requestId, Long assigneeId, Long assignerId);

    /**
     * Rejects a request, logs the reason, and updates status to REJECTED
     * @param requestId The request ID
     * @param employeeId The employee performing the action
     * @param reason The rejection reason
     */
    void rejectRequest(Long requestId, Long employeeId, String reason);

    /**
     * Approves a request, logs the action, and updates status to COMPLETED
     * @param requestId The request ID
     * @param employeeId The employee performing the action
     */
    void approveRequest(Long requestId, Long employeeId);

    /**
     * Retrieves appointments for the employee's department
     * @param employeeId The employee ID
     * @return List of Appointments
     */
    List<AppointmentView> getAppointmentsForEmployeeDepartment(Long employeeId);

    /**
     * Marks an appointment as CONFIRMED
     * @param appointmentId The appointment ID
     */
    void confirmAppointment(Long appointmentId);

    /**
     * Marks an appointment as CANCELLED
     * @param appointmentId The appointment ID
     */
    void cancelAppointment(Long appointmentId);

    /**
     * Changes the date of an appointment and notifies the citizen
     * @param appointmentId The appointment ID
     * @param rescheduledDateTime The new date and time
     */
    void rescheduleAppointment(Long appointmentId, java.time.LocalDateTime rescheduledDateTime);

    /**
     * Finds an employee by their username
     * @param username The username of the logged-in user
     * @return The EmployeeView DTO
     */
    Employee getEmployeeByUsername(String username);

    /**
     * Returns a document based on the id
     * @param documentId the documents id
     * @return  the returned document
     */
    RequestDocument getDocument(Long documentId);

    /**
     * Returns the time schedule of a department that an employee works on
     * @param employeeId the id of the employee that works on the department
     * @return A List of DepartmentScheduleView
     */
    List<DepartmentScheduleView> getDepartmentScheduleForEmployee(Long employeeId);
}
