package gr.hua.dit.mycitygov.core.service;


import gr.hua.dit.mycitygov.core.model.Department;
import gr.hua.dit.mycitygov.core.model.DepartmentSchedule;
import gr.hua.dit.mycitygov.core.model.RequestType;
import gr.hua.dit.mycitygov.core.service.model.*;

import java.util.List;

/**
 * Service interface defining the business logic for Admin operations
 */
public interface AdminService {

    List<RequestTypeView> getAllRequestTypes();

    List<DepartmentView> getAllDepartments();

    SystemStatistics getSystemStatistics();

    List<UserView> getAllUsers();

    Department getDepartmentById(Long id);

    List<DepartmentScheduleView> getDepartmentSchedule(Long departmentId);

    void createDepartment(String name, String description);

    void updateDepartmentSchedule(Long departmentId, java.time.DayOfWeek day, java.time.LocalTime start, java.time.LocalTime end);

    void updateDepartmentInfo(Long id, String name, String description);

    void createRequestType(CreateRequestTypeRequest request);

    void toggleRequestTypeStatus(Long id);

    void reassignRequestType(Long id, Long newDepartmentId);

}
