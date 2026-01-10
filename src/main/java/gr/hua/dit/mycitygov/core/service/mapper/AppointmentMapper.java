package gr.hua.dit.mycitygov.core.service.mapper;

import gr.hua.dit.mycitygov.core.model.Appointment;
import gr.hua.dit.mycitygov.core.service.model.AppointmentView;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    public AppointmentView toAppointmentView(Appointment appointment) {
        if (appointment == null) return null;

        return new AppointmentView(
                appointment.getId(),
                appointment.getDepartment().getName(), // Παίρνουμε το όνομα του τμήματος
                appointment.getAppointmentDate(),
                appointment.getStatus().name(),
                appointment.getCitizen().getFirstName() + " " + appointment.getCitizen().getLastName()
        );
    }
}