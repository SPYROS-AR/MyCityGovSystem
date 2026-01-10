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
        AppointmentView appointmentView = new AppointmentView();
        appointmentView.setId(appointment.getId());
        appointmentView.setAppointmentDate(appointment.getAppointmentDate());
        appointmentView.setStatus(appointment.getStatus().name());
        return appointmentView;

    }
}
