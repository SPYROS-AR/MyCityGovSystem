package gr.hua.dit.mycitygov.core.service.mapper;

import gr.hua.dit.mycitygov.core.model.Appointment;
import gr.hua.dit.mycitygov.core.service.model.AppointmentView;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    private final CitizenMapper citizenMapper;

    public AppointmentMapper(CitizenMapper citizenMapper) {
        this.citizenMapper = citizenMapper;
    }

    public AppointmentView toAppointmentView(Appointment appointment) {
        if (appointment == null) return null;

        return new AppointmentView(
                appointment.getId(),
                appointment.getDepartment().getName(),
                appointment.getAppointmentDate(),
                appointment.getStatus().name(),
                citizenMapper.toDto(appointment.getCitizen()));
    }
}
