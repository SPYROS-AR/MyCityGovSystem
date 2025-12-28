package gr.hua.dit.mycitygov.core.service;

import gr.hua.dit.mycitygov.core.model.Employee;
import gr.hua.dit.mycitygov.core.model.Request;
import gr.hua.dit.mycitygov.core.service.model.EmployeeView;

import java.util.List;

public interface EmployeeService {
    List<Employee> getAllEmployees();
    List<Request> getRequestsForEmployeeDepartment(Long employeeId);
    void updateRequestStatus(Long requestId, Request.Status newStatus);
    void assignRequestToEmployee(Long requestId, Long employeeId);

}
