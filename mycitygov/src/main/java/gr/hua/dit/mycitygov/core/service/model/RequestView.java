package gr.hua.dit.mycitygov.core.service.model;

import gr.hua.dit.mycitygov.core.model.RequestLog;

import java.time.LocalDateTime;
import java.util.List;

public record RequestView(
        Long id,
        String protocolNumber,
        String requestTypeName,
        String status,
        String description,
        LocalDateTime submittedDate,
        CitizenView citizen,
        EmployeeView assignedEmployee,
        List<RequestLog> logs,
        DepartmentView department,
        LocalDateTime dueDate,
        List<RequestDocumentView> documents
) {}