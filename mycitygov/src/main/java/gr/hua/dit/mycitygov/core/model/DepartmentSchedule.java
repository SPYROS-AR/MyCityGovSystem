package gr.hua.dit.mycitygov.core.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Getter @Setter
@Table(name = "department_schedules", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"department_id", "day_of_week"})
})
public class DepartmentSchedule {

    @Id
    @Setter(AccessLevel.NONE) // Do not create setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    public DepartmentSchedule() {}

    public DepartmentSchedule(Department department, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        this.department = department;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
