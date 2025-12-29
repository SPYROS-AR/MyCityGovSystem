package gr.hua.dit.mycitygov.core.repository;

import gr.hua.dit.mycitygov.core.model.Employee;
import gr.hua.dit.mycitygov.core.service.model.EmployeeView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee,Long> {

    /**
     * Returns all Employees on a Department
     */
    List<Employee> findByDepartmentId(Long departmentId);

    //Employee findByEmployeeId(Long employeeId);
}
