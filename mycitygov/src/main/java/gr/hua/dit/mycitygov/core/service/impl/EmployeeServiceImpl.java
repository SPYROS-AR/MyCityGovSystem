package gr.hua.dit.mycitygov.core.service.impl;

import gr.hua.dit.mycitygov.core.model.Employee;
import gr.hua.dit.mycitygov.core.repository.EmployeeRepository;
import gr.hua.dit.mycitygov.core.repository.UserRepository;
import gr.hua.dit.mycitygov.core.service.EmployeeService;
import gr.hua.dit.mycitygov.core.service.model.EmployeeView;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, UserRepository userRepository) {
        if (employeeRepository == null) throw new NullPointerException("employeeRepository cannot be null");
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }
}
