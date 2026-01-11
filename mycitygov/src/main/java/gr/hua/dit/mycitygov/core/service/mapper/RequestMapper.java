package gr.hua.dit.mycitygov.core.service.mapper;

import gr.hua.dit.mycitygov.core.model.Request;
import gr.hua.dit.mycitygov.core.model.RequestLog;
import gr.hua.dit.mycitygov.core.service.model.CitizenView;
import gr.hua.dit.mycitygov.core.service.model.EmployeeView;
import gr.hua.dit.mycitygov.core.service.model.RequestView;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RequestMapper {
    private final CitizenMapper citizenMapper;
    private final EmployeeMapper employeeMapper;

    public RequestMapper(CitizenMapper citizenMapper, EmployeeMapper employeeMapper) {
        this.citizenMapper = citizenMapper;
        this.employeeMapper = employeeMapper;
    }

    public RequestView toDto(final Request request) {
        if(request == null) return null;

        String assignedEmployeeName = "-";
        if (request.getAssignedEmployee() != null) {
            assignedEmployeeName = request.getAssignedEmployee().getFirstName() + " " + request.getAssignedEmployee().getLastName();
        }

        return new RequestView(
                request.getId(),
                request.getProtocolNumber(),
                request.getRequestType().getName(),
                request.getStatus().name(),
                request.getDescription(),
                request.getSubmittedDate(),
                citizenMapper.toDto(request.getCitizen()),
                employeeMapper.toDto(request.getAssignedEmployee()),
                request.getLogs()
        );
    }
}