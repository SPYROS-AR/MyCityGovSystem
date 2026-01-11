package gr.hua.dit.mycitygov.core.service.model;

import java.time.LocalDateTime;

public record AppointmentView(
        Long id,
        String departmentName,
        LocalDateTime date,
        String status,
        String citizenName
) {}