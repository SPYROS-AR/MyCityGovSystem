package gr.hua.dit.mycitygov.core.service.model;

import java.time.LocalDateTime;

public record AppointmentView(
        Long id,
        String departmentName,
        LocalDateTime appointmentDate,
        String status,
        CitizenView citizen
) {}