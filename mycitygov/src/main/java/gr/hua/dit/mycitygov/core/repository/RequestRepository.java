package gr.hua.dit.mycitygov.core.repository;

import gr.hua.dit.mycitygov.core.model.Employee;
import gr.hua.dit.mycitygov.core.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    // Return all Requests that are assigned to a specific employee
    List<Request> findByAssignedEmployeeId(Long employeeId);
    // Return all Requests of a department
    List<Request> findByDepartmentId(Long departmentId);
    // Returns a request based on the protocolNumber
    Optional<Request> findByProtocolNumber(String protocolNumber);
    // Returns all Requests submitted by a citizen
    @Query("SELECT r FROM Request r LEFT JOIN FETCH r.documents WHERE r.citizen.id = :citizenId")
    List<Request> findByCitizenId(@Param("citizenId") Long citizenId);


}