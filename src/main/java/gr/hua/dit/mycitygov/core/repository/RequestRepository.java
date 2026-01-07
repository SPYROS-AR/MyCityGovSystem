package gr.hua.dit.mycitygov.core.repository;

import gr.hua.dit.mycitygov.core.model.Employee;
import gr.hua.dit.mycitygov.core.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    // Return all Requests that are assigned to a specific employee
    List<Request> findByAssignedEmployeeId(Long employeeId);
    // Return all Requests of a department
    List<Request> findByDepartmentId(Long departmentId);
}
