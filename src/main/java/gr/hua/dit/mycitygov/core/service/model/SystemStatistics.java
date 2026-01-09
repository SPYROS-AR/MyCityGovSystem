package gr.hua.dit.mycitygov.core.service.model;

import java.util.Map;

public record SystemStatistics(
        long totalRequests,
        long pendingRequests,
        long completedRequests,
        long totalCitizens,
        Map<String, Long> requestsByDepartment,
        long overdueRequests
) {}
