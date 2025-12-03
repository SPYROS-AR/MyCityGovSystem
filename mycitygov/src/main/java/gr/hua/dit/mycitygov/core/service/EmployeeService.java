package gr.hua.dit.mycitygov.core.service;

import gr.hua.dit.mycitygov.core.model.Employee;

import java.util.List;

public interface EmployeeService {
    List<Employee> getAllEmployees();
    Employee saveEmployee(Employee employee);
}
