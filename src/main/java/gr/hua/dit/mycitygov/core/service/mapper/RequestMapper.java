package gr.hua.dit.mycitygov.core.service.mapper;

import gr.hua.dit.mycitygov.core.model.Request;
import gr.hua.dit.mycitygov.core.service.model.RequestView;
import org.springframework.stereotype.Component;

@Component
public class RequestMapper {
    public RequestView convertRequestToRequestView(final Request request) {
        if(request == null) return null;

        String assignedEmployeeName = "-";
        if (request.getAssignedEmployee() != null) {
            assignedEmployeeName = request.getAssignedEmployee().getFirstName() + " " + request.getAssignedEmployee().getLastName();
        }

        return new RequestView(
                request.getId(),
                request.getProtocolNumber(),
                request.getRequestType().getName(),
                request.getStatus(),
                request.getDescription(),
                request.getCitizen().getFirstName() + " " + request.getCitizen().getLastName(),
                assignedEmployeeName,
                request.getSubmittedDate()
        );
    }
}