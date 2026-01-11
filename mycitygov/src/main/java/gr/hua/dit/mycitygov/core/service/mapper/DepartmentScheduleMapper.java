package gr.hua.dit.mycitygov.core.service.mapper;

import gr.hua.dit.mycitygov.core.model.DepartmentSchedule;
import gr.hua.dit.mycitygov.core.service.model.DepartmentScheduleView;
import org.springframework.stereotype.Component;

@Component
public class DepartmentScheduleMapper {

    public DepartmentScheduleView toDto(DepartmentSchedule schedule) {
        if (schedule == null) return null;

        return new DepartmentScheduleView(
                schedule.getId(),
                schedule.getDayOfWeek(),
                schedule.getStartTime(),
                schedule.getEndTime()
        );
    }
}
