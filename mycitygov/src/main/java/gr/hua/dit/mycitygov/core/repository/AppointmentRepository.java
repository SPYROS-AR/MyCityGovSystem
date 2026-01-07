package gr.hua.dit.mycitygov.core.repository;

import gr.hua.dit.mycitygov.core.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    // get all appointments of a department
    List<Appointment> findByDepartmentId(Long departmentId);
}
