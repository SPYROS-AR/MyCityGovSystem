package gr.hua.dit.mycitygov.core.service;


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

    void createRequestType(CreateRequestTypeRequest request);

    void toggleRequestTypeStatus(Long id);

    void reassignRequestType(Long id, Long newDepartmentId);

}
