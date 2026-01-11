package gr.hua.dit.mycitygov.core.model;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter @Getter
@Table(name = "requests")
public class Request {
    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String protocolNumber;

    private LocalDateTime submittedDate;
    private LocalDateTime dueDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "citizen_id", nullable = false)
    private Citizen citizen;

    @ManyToOne
    @JoinColumn(name = "request_type_id", nullable = false)
    private RequestType requestType;

    @ManyToOne
    @JoinColumn(name = "assigned_employee_id")
    private Employee assignedEmployee;

    // list with request history
    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL)
    private List<RequestLog> logs = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    public Request() {
    }

    public Request(String protocolNumber,
                   LocalDateTime submittedDate,
                   LocalDateTime dueDate,
                   Status status,
                   String description,
                   Citizen citizen,
                   RequestType requestType,
                   Employee assignedEmployee,
                   List<RequestLog> logs,
                   Department department) {
        this.protocolNumber = protocolNumber;
        this.submittedDate = submittedDate;
        this.dueDate = dueDate;
        this.status = status;
        this.description = description;
        this.citizen = citizen;
        this.requestType = requestType;
        this.assignedEmployee = assignedEmployee;
        this.logs = (logs != null) ? logs : new ArrayList<>();
        this.department = department;
    }

    public enum Status {
        SUBMITTED,
        RECEIVED,
        PROCESSING,
        PENDING_DOCS,
        COMPLETED,
        REJECTED
    }
}
