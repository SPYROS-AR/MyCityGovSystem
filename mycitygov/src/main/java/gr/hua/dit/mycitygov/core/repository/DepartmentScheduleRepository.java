package gr.hua.dit.mycitygov.core.repository;

import gr.hua.dit.mycitygov.core.model.DepartmentSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentScheduleRepository extends JpaRepository<DepartmentSchedule, Long> {

    List<DepartmentSchedule> findByDepartmentId(Long departmentId);
    // Find the schedule for a specific day and specific department
    Optional<DepartmentSchedule> findByDepartmentIdAndDayOfWeek(Long departmentId, DayOfWeek dayOfWeek);
}