package gr.hua.dit.mycitygov.core.service.model;

import gr.hua.dit.mycitygov.core.model.Department;

public record EmployeeView(
        Long id,
        String username,
        String firstName,
        String lastName,
        String email,
        Department department
) {}
