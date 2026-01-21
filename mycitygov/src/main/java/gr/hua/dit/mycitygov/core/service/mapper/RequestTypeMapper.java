package gr.hua.dit.mycitygov.core.service.mapper;

import gr.hua.dit.mycitygov.core.model.RequestType;
import gr.hua.dit.mycitygov.core.service.model.RequestTypeView;
import org.springframework.stereotype.Component;

@Component
public class RequestTypeMapper {

    public RequestTypeView toDto(RequestType requestType) {
        return new RequestTypeView(
                requestType.getId(),
                requestType.getName(),
                requestType.getRequestCategory().toString(), // Enum -> String
                requestType.getRequiredDocuments(),
                requestType.getDepartment() != null ? requestType.getDepartment().getName() : "N/A",
                requestType.getSlaDays(),
                requestType.isActive()
        );
    }
}