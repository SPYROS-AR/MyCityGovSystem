package gr.hua.dit.mycitygov.core.service.mapper;

import gr.hua.dit.mycitygov.core.model.Employee;
import gr.hua.dit.mycitygov.core.service.model.EmployeeView;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {
    public EmployeeView convertEmployeeToEmployeeView(final Employee employee) {
        if(employee == null) return null;

        return new EmployeeView(
                employee.getId(),
                employee.getUsername(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getDepartment()
        );
    }
}
