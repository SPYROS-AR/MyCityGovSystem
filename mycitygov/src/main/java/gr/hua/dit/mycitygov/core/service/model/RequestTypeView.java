package gr.hua.dit.mycitygov.core.service.model;

public record RequestTypeView(
        Long id,
        String name,
        String category,    // Enum as  String
        String requiredDocuments,
        String departmentName,
        Integer slaDays,
        Boolean isActive
) {}
