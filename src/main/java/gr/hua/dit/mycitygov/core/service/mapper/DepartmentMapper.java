package gr.hua.dit.mycitygov.core.service.mapper;
import gr.hua.dit.mycitygov.core.model.Department;
import gr.hua.dit.mycitygov.core.service.model.DepartmentView;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapper {
    public DepartmentView toDto(Department department) {
        if (department == null) return null;

        return new DepartmentView(
                department.getId(),
                department.getName(),
                department.getDescription()
        );
    }
}
