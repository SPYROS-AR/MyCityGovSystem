package gr.hua.dit.mycitygov.core.service;

import gr.hua.dit.mycitygov.core.model.Employee;
import gr.hua.dit.mycitygov.core.service.model.EmployeeView;

import java.util.List;

public interface EmployeeService {
    List<EmployeeView> getAllEmployees();
}
