package gr.hua.dit.mycitygov.core.service.impl;

import gr.hua.dit.mycitygov.core.model.Employee;
import gr.hua.dit.mycitygov.core.repository.EmployeeRepository;
import gr.hua.dit.mycitygov.core.repository.UserRepository;
import gr.hua.dit.mycitygov.core.service.EmployeeService;

import java.util.List;

public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        if (employeeRepository == null) throw new NullPointerException("employeeRepository cannot be null");
        this.employeeRepository = employeeRepository;
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee saveEmployee(Employee employee) {
        // 1. Check if user exists


        // 2. Encode Password

        // 3. Save
        return employeeRepository.save(employee);
    }
}
