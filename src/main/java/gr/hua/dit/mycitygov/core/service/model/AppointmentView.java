package gr.hua.dit.mycitygov.core.service.model;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

public class AppointmentView {
    private Long id;
    private LocalDateTime appointmentDate;
    private String status;
    private CitizenView citizen;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDateTime appointmentDate) { this.appointmentDate = appointmentDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public CitizenView getCitizen() { return citizen; }
    public void setCitizen(CitizenView citizen) { this.citizen = citizen; }
}
