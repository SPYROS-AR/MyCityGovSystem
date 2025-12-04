package gr.hua.dit.mycitygov.core.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@Table(name = "request_logs")
public class RequestLog {

    @Id
    @Setter(AccessLevel.NONE) // Do not create setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime actionDate;

    // Χρησιμοποιούμε το Request.Status
    @Enumerated(EnumType.STRING)
    private Request.Status oldStatus;

    @Enumerated(EnumType.STRING)
    private Request.Status newStatus;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @ManyToOne
    @JoinColumn(name = "request_id")
    private Request request;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    public RequestLog() {
    }

    public RequestLog(LocalDateTime actionDate, Request.Status oldStatus, Request.Status newStatus, String comment, Request request, Employee employee) {
        this.actionDate = actionDate;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.comment = comment;
        this.request = request;
        this.employee = employee;
    }
}
