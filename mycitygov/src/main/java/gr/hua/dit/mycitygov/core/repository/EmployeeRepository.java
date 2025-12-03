package gr.hua.dit.mycitygov.core.repository;

import gr.hua.dit.mycitygov.core.model.Employee;

import java.util.List;

public interface EmployeeRepository {

    /**
     * Returns all Employees on a Department
     */
    List<Employee> findByDepartmentId();
}
