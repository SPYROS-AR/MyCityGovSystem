package gr.hua.dit.mycitygov.core.service;


import gr.hua.dit.mycitygov.core.model.Department;
import gr.hua.dit.mycitygov.core.service.model.*;

import java.util.List;

/**
 * Service interface for administrative business logic.
 * Defines operations for managing municipal departments, request types,
 * and overall system configuration.
 */
public interface AdminService {

    /**
     * Retrieves all request types available in the system.
     *
     * @return A list of {@link RequestTypeView} DTOs representing all request types.
     */
    List<RequestTypeView> getAllRequestTypes();

    /**
     * Retrieves all departments registered in the municipality.
     *
     * @return A list of {@link DepartmentView} DTOs representing all departments.
     */
    List<DepartmentView> getAllDepartments();

    /**
     * Calculates and retrieves system-wide statistics.
     * <p>
     * This includes total requests, pending requests, completed requests,
     * total registered citizens, requests per department, and overdue requests.
     * </p>
     *
     * @return A {@link SystemStatistics} object containing the aggregated data.
     */
    SystemStatistics getSystemStatistics();

    /**
     * Retrieves a list of all users registered in the system (Citizens, Employees, Admins).
     *
     * @return A list of {@link UserView} DTOs.
     */
    List<UserView> getAllUsers();

    /**
     * Retrieves the full entity of a specific department by its ID.
     *
     * @param id The unique identifier of the department.
     * @return The {@link Department} entity.
     * @throws RuntimeException if the department with the given ID is not found.
     */
    Department getDepartmentById(Long id);

    /**
     * Retrieves the weekly schedule for a specific department.
     *
     * @param departmentId The ID of the department.
     * @return A sorted list of {@link DepartmentScheduleView} DTOs representing the opening hours for each day.
     */
    List<DepartmentScheduleView> getDepartmentSchedule(Long departmentId);

    /**
     * Creates a new department in the system.
     *
     * @param name        The unique name of the department.
     * @param description A brief description of the department's responsibilities.
     * @throws RuntimeException if a department with the same name already exists.
     */
    void createDepartment(String name, String description);

    /**
     * Updates or creates a schedule entry for a specific day for a department.
     *
     * @param departmentId The ID of the department.
     * @param day          The day of the week (e.g., MONDAY).
     * @param start        The opening time.
     * @param end          The closing time.
     * @throws IllegalArgumentException if the start time is after the end time.
     */
    void updateDepartmentSchedule(Long departmentId, java.time.DayOfWeek day, java.time.LocalTime start, java.time.LocalTime end);

    /**
     * Updates the basic information (name and description) of an existing department.
     *
     * @param id          The ID of the department to update.
     * @param name        The new name for the department.
     * @param description The new description for the department.
     */
    void updateDepartmentInfo(Long id, String name, String description);

    /**
     * Creates a new request type (service) that citizens can apply for.
     *
     * @param request The DTO containing details like name, category, SLA days, and the responsible department.
     */
    void createRequestType(CreateRequestTypeRequest request);

    /**
     * Toggles the active status of a request type.
     * <p>
     * If active, it becomes inactive, preventing new applications. If inactive, it becomes active.
     * </p>
     *
     * @param id The ID of the request type.
     */
    void toggleRequestTypeStatus(Long id);

    /**
     * Reassigns a request type to a different department.
     *
     * @param id              The ID of the request type to reassign.
     * @param newDepartmentId The ID of the new department that will handle this request type.
     */
    void reassignRequestType(Long id, Long newDepartmentId);
}
