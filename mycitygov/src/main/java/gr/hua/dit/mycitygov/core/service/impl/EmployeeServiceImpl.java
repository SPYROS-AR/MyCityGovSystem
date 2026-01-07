package gr.hua.dit.mycitygov.core.service.impl;

import gr.hua.dit.mycitygov.core.model.Appointment;
import gr.hua.dit.mycitygov.core.model.Employee;
import gr.hua.dit.mycitygov.core.model.Request;
import gr.hua.dit.mycitygov.core.model.RequestLog;
import gr.hua.dit.mycitygov.core.repository.AppointmentRepository;
import gr.hua.dit.mycitygov.core.repository.EmployeeRepository;
import gr.hua.dit.mycitygov.core.repository.RequestRepository;
import gr.hua.dit.mycitygov.core.repository.UserRepository;
import gr.hua.dit.mycitygov.core.service.EmailService;
import gr.hua.dit.mycitygov.core.service.EmployeeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Implementation of the EmployeeService interface
 * Handles the core business logic, database transactions, and communication with the EmailService
 */
@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final AppointmentRepository appointmentRepository;
    private final EmailService emailService;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                               UserRepository userRepository,
                               RequestRepository requestRepository,
                               AppointmentRepository appointmentRepository,
                               EmailService emailService) {
        if (employeeRepository == null) throw new NullPointerException("employeeRepository cannot be null");
        if (userRepository == null) throw new NullPointerException("userRepository cannot be null");
        if (requestRepository == null) throw new NullPointerException("requestRepository cannot be null");
        if (appointmentRepository == null) throw new NullPointerException("appointmentRepository cannot be null");
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
        this.appointmentRepository = appointmentRepository;
        this.emailService = emailService;
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public List<Request> getRequestsForEmployeeDepartment(Long employeeId) {
        Employee employee = employeeRepository
                .findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee with id " + employeeId + " not found"));
        return requestRepository.findByDepartmentId(employee.getDepartment().getId());
    }

    @Override
    public List<Request> getRequestsForDepartment(Long departmentId) {
        return requestRepository.findByDepartmentId(departmentId);
    }

    @Override
    public void updateRequestStatus(Long requestId, Request.Status newStatus) {
        Request request = requestRepository
                .findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        request.setStatus(newStatus);   // set new status
        requestRepository.save(request);// update the request in the db
    }

    @Override
    public void assignRequestToEmployee(Long requestId, Long employeeId) {
        Request request = requestRepository
                .findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request with id "+ requestId +"not found"));
        Employee employee = employeeRepository
                .findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee with id " + employeeId + " not found"));
        request.setAssignedEmployee(employee);          // update request to include employee
        request.setStatus(Request.Status.PROCESSING);   // change request status to processing
        requestRepository.save(request);                // update request
    }

    @Override
    @Transactional
    public void rejectRequest(Long requestId, Long employeeId,  String reason) {
        Request request = requestRepository
                .findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // store old status before update to put it in the log
        Request.Status oldStatus = request.getStatus();
        request.setStatus(Request.Status.REJECTED);

        // create log for history tracking
        RequestLog log = new RequestLog();
        log.setActionDate(LocalDateTime.now());
        log.setComment("Rejected:" + reason);
        log.setOldStatus(oldStatus);
        log.setNewStatus(Request.Status.REJECTED);
        log.setRequest(request);
        log.setEmployee(employee);

        request.getLogs().add(log);     // add to list
        requestRepository.save(request);

        emailService.sendEmail(
                request.getCitizen().getEmail(),
                "Request Update " + request.getProtocolNumber(),
                "Dear Citizen,\n\nYour request has been rejected.\nReason: " + reason + "\n\nMyCityGov"
        );
    }

    @Override
    @Transactional
    public void approveRequest(Long requestId,  Long employeeId) {
        Request request = requestRepository
                .findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Request.Status oldStatus = request.getStatus();
        request.setStatus(Request.Status.COMPLETED);

        // create log
        RequestLog log = new RequestLog();
        log.setActionDate(LocalDateTime.now());
        log.setComment("Request:" + request.getStatus());
        log.setOldStatus(oldStatus);
        log.setNewStatus(Request.Status.COMPLETED);
        log.setRequest(request);
        log.setEmployee(employee);

        request.getLogs().add(log);
        requestRepository.save(request);

        emailService.sendEmail(
                request.getCitizen().getEmail(),
                "Request Completion " + request.getProtocolNumber(),
                "Dear Citizen,\n\nYour request has been accepted and completed!\n\nMyCityGov"
        );
    }

    @Override
    @Transactional
    public Request getRequestById(Long id) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request with id " + id + " not found"));
        // fetch logs immediately to avoid LazyInitializationException in the controller/view
        request.getLogs().size();
        return request;
    }

    @Override
    public List<Appointment> getAppointmentsForEmployeeDepartment(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow();
        return appointmentRepository.findByDepartmentId(employee.getDepartment().getId());
    }

    @Override
    @Transactional
    public void confirmAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow();
        appointment.setStatus(Appointment.AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);

        emailService.sendEmail(
                appointment.getCitizen().getEmail(),
                "Appointment Confirmation",
                "Your appointment has been confirmed for: " + appointment.getAppointmentDate()
        );
    }

    @Override
    @Transactional
    public void cancelAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow();
        appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);

        emailService.sendEmail(
                appointment.getCitizen().getEmail(),
                "Appointment Cancelled",
                "Your Appointment has been cancelled."
        );
    }

    @Override
    @Transactional
    public void rescheduleAppointment(Long appointmentId, LocalDateTime rescheduledDateTime) {
        Appointment appointment = appointmentRepository
                .findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment with id " + appointmentId + " not found"));
        appointment.setAppointmentDate(rescheduledDateTime);
        // reset status to SCHEDULED if it was rejected before
        appointment.setStatus(Appointment.AppointmentStatus.SCHEDULED);

        appointmentRepository.save(appointment);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        emailService.sendEmail(
                appointment.getCitizen().getEmail(),
                "Appointment Date Changed",
                "Dear citizen,\n\nYour Appointment changed date.\nNew Date: " + rescheduledDateTime.format(formatter) + "\n\nMyCityGov"
        );

    }
}
