package gr.hua.dit.mycitygov.core.repository;

import gr.hua.dit.mycitygov.core.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    // get all appointments of a department
    List<Appointment> findByDepartmentId(Long departmentId);

    // get all appointments of a citizen
    List<Appointment> findByCitizenId(Long citizenId);

    boolean existsByDepartmentIdAndAppointmentDateAndStatusIn(
            Long departmentId,
            LocalDateTime appointmentDate,
            Collection<Appointment.AppointmentStatus> statuses
    );
}