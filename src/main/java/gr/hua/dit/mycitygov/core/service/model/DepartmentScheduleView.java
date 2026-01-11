package gr.hua.dit.mycitygov.core.service.model;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record DepartmentScheduleView(
        Long id,
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime
) {}
