package gr.hua.dit.mycitygov.core.service.model;

import gr.hua.dit.mycitygov.core.model.Request;

import java.time.LocalDateTime;

public record RequestView(
        Long id,
        String protocolNumber,
        String requestTypeName,
        Request.Status status,
        String description,
        String citizenName,
        String assignedEmployeeName,
        LocalDateTime submittedDate
) {}