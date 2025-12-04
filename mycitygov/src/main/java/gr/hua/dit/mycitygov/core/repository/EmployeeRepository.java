package gr.hua.dit.mycitygov.core.repository;

import gr.hua.dit.mycitygov.core.model.Employee;
import gr.hua.dit.mycitygov.core.service.model.EmployeeView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeView,Long> {

    /**
     * Returns all Employees on a Department
     */
    List<EmployeeView> findByDepartmentId(Long departmentId);
}
