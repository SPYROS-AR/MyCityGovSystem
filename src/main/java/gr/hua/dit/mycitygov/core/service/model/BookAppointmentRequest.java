package gr.hua.dit.mycitygov.core.service.model;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record BookAppointmentRequest(
        @NotNull Long departmentId,
        @NotNull LocalDateTime appointmentDate
) {
}