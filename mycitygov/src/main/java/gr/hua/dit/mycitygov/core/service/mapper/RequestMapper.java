package gr.hua.dit.mycitygov.core.service.mapper;

import gr.hua.dit.mycitygov.core.model.Request;
import gr.hua.dit.mycitygov.core.model.RequestLog;
import gr.hua.dit.mycitygov.core.service.model.CitizenView;
import gr.hua.dit.mycitygov.core.service.model.EmployeeView;
import gr.hua.dit.mycitygov.core.service.model.RequestDocumentView;
import gr.hua.dit.mycitygov.core.service.model.RequestView;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RequestMapper {
    private final CitizenMapper citizenMapper;
    private final EmployeeMapper employeeMapper;
    private final DepartmentMapper departmentMapper;

    public RequestMapper(CitizenMapper citizenMapper,
                         EmployeeMapper employeeMapper,
                         DepartmentMapper departmentMapper) {
        this.citizenMapper = citizenMapper;
        this.employeeMapper = employeeMapper;
        this.departmentMapper = departmentMapper;
    }

    public RequestView toDto(final Request request) {
        if(request == null) return null;

        String assignedEmployeeName = "-";
        if (request.getAssignedEmployee() != null) {
            assignedEmployeeName = request
                    .getAssignedEmployee()
                    .getFirstName() + " " + request.getAssignedEmployee().getLastName();
        }

        List<RequestDocumentView> documents = request
                .getDocuments()
                .stream()
                .map(document -> new RequestDocumentView(
                        document.getId(),
                        document.getFileName(),
                        document.getFileName()))
                .collect(Collectors.toList());

        return new RequestView(
                request.getId(),
                request.getProtocolNumber(),
                request.getRequestType().getName(),
                request.getStatus().name(),
                request.getDescription(),
                request.getSubmittedDate(),
                citizenMapper.toDto(request.getCitizen()),
                employeeMapper.toDto(request.getAssignedEmployee()),
                request.getLogs(),
                departmentMapper.toDto(request.getDepartment()),
                request.getDueDate(),
                documents
        );
    }
}