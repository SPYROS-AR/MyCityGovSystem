package gr.hua.dit.mycitygov.core.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter @Getter
@Table(name = "appointments")
public class Appointment {

    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime appointmentDate;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    @ManyToOne
    @JoinColumn(name = "citizen_id", nullable = false)
    private Citizen citizen;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    public Appointment() {
    }

    public Appointment(Department department) {
        this.department = department;
    }

    public Appointment(LocalDateTime appointmentDate,
                       AppointmentStatus status,
                       Citizen citizen,
                       Department department) {
        this.appointmentDate = appointmentDate;
        this.status = status;
        this.citizen = citizen;
        this.department = department;
    }

    public enum AppointmentStatus {
        SCHEDULED,
        COMPLETED,
        CANCELLED
    }
}
