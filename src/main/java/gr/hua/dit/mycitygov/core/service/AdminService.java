package gr.hua.dit.mycitygov.core.service;


import gr.hua.dit.mycitygov.core.model.Department;
import gr.hua.dit.mycitygov.core.model.RequestType;
import gr.hua.dit.mycitygov.core.model.User;
import gr.hua.dit.mycitygov.core.service.model.CreateRequestTypeRequest;
import gr.hua.dit.mycitygov.core.service.model.SystemStatistics;

import java.util.List;

/**
 * Service interface defining the business logic for Admin operations
 */
public interface AdminService {

    List<RequestType> getAllRequestTypes();

    List<Department> getAllDepartments();

    SystemStatistics getSystemStatistics();

    List<User> getAllUsers();

    void createRequestType(CreateRequestTypeRequest request);

    void toggleRequestTypeStatus(Long id);

    void reassignRequestType(Long id, Long newDepartmentId);

}
