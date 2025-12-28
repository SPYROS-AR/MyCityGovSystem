package gr.hua.dit.mycitygov.core.service.impl;

import gr.hua.dit.mycitygov.core.model.Employee;
import gr.hua.dit.mycitygov.core.model.Request;
import gr.hua.dit.mycitygov.core.repository.EmployeeRepository;
import gr.hua.dit.mycitygov.core.repository.RequestRepository;
import gr.hua.dit.mycitygov.core.repository.UserRepository;
import gr.hua.dit.mycitygov.core.service.EmployeeService;
import gr.hua.dit.mycitygov.core.service.model.EmployeeView;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                               UserRepository userRepository,
                               RequestRepository requestRepository) {
        if (employeeRepository == null) throw new NullPointerException("employeeRepository cannot be null");
        if (userRepository == null) throw new NullPointerException("userRepository cannot be null");
        if (requestRepository == null) throw new NullPointerException("requestRepository cannot be null");
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    // Give all the requests of the department that the employee works on
    @Override
    public List<Request> getRequestsForEmployeeDepartment(Long employeeId) {
        Employee employee = employeeRepository
                .findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee with id " + employeeId + " not found"));
        return requestRepository.findByDepartmentId(employee.getDepartment().getId());
    }

    // changes the status of a request to a new status
    @Override
    public void updateRequestStatus(Long requestId, Request.Status newStatus) {
        Request request = requestRepository
                .findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        request.setStatus(newStatus);   // set new status
        requestRepository.save(request);// update the request in the db
    }

    // the employee assigns a request to himself
    @Override
    public void assignRequestToEmployee(Long requestId, Long employeeId) {
        Request request = requestRepository
                .findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request with id "+ requestId +"not found"));
        Employee employee = employeeRepository
                .findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee with id " + employeeId + " not found"));
        request.setAssignedEmployee(employee);          // update request to include employee
        request.setStatus(Request.Status.PROCESSING);   // change request status to processing
        requestRepository.save(request);                // update request
    }


}
