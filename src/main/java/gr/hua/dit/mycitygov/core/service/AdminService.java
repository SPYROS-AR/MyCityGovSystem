package gr.hua.dit.mycitygov.core.service;


import gr.hua.dit.mycitygov.core.model.Department;
import gr.hua.dit.mycitygov.core.model.RequestType;

import java.util.List;

/**
 * Service interface defining the business logic for Admin operations
 */
public interface AdminService {

    List<RequestType> getAllRequestTypes;

    List<Department> getAllDepartments;

    

}
