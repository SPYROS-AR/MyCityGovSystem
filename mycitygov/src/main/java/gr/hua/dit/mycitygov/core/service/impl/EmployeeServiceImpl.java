package gr.hua.dit.mycitygov.core.service.impl;

import gr.hua.dit.mycitygov.core.model.*;
import gr.hua.dit.mycitygov.core.port.SmsNotificationPort;
import gr.hua.dit.mycitygov.core.repository.*;
import gr.hua.dit.mycitygov.core.service.EmployeeService;
import gr.hua.dit.mycitygov.core.service.mapper.AppointmentMapper;
import gr.hua.dit.mycitygov.core.service.mapper.DepartmentScheduleMapper;
import gr.hua.dit.mycitygov.core.service.mapper.EmployeeMapper;
import gr.hua.dit.mycitygov.core.service.mapper.RequestMapper;
import gr.hua.dit.mycitygov.core.service.model.AppointmentView;
import gr.hua.dit.mycitygov.core.service.model.DepartmentScheduleView;
import gr.hua.dit.mycitygov.core.service.model.EmployeeView;
import gr.hua.dit.mycitygov.core.service.model.RequestView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the EmployeeService interface
 * Handles the core business logic, database transactions, and communication with the EmailService
 */
@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final RequestRepository requestRepository;
    private final AppointmentRepository appointmentRepository;
    private final SmsNotificationPort smsPort;
    private final DepartmentScheduleRepository departmentScheduleRepository;
    private final RequestDocumentRepository requestDocumentRepository;

    // mappers
    private final RequestMapper requestMapper;
    private final AppointmentMapper appointmentMapper;
    private final EmployeeMapper employeeMapper;
    private final DepartmentScheduleMapper departmentScheduleMapper;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                               UserRepository userRepository,
                               RequestRepository requestRepository,
                               AppointmentRepository appointmentRepository,
                               RequestMapper requestMapper,
                               AppointmentMapper appointmentMapper,
                               EmployeeMapper employeeMapper,
                               SmsNotificationPort smsPort,
                               DepartmentScheduleRepository departmentScheduleRepository,
                               RequestDocumentRepository requestDocumentRepository,
                               DepartmentScheduleMapper departmentScheduleMapper) {
        if (employeeRepository == null) throw new NullPointerException("employeeRepository cannot be null");
        if (userRepository == null) throw new NullPointerException("userRepository cannot be null");
        if (requestRepository == null) throw new NullPointerException("requestRepository cannot be null");
        if (appointmentRepository == null) throw new NullPointerException("appointmentRepository cannot be null");
        if (requestMapper == null) throw new NullPointerException("requestMapper cannot be null");
        if (appointmentMapper == null) throw new NullPointerException("appointmentMapper cannot be null");
        if (employeeMapper == null) throw new NullPointerException("employeeMapper cannot be null");
        if (smsPort == null) throw new NullPointerException("smsPort cannot be null");
        if (departmentScheduleRepository == null) throw new NullPointerException("departmentScheduleRepository cannot be null");
        if (requestDocumentRepository == null) throw new NullPointerException("requestDocumentRepository cannot be null");
        if (departmentScheduleMapper == null) throw new NullPointerException("departmentScheduleMapper cannot be null");
        this.employeeRepository = employeeRepository;
        this.requestRepository = requestRepository;
        this.appointmentRepository = appointmentRepository;
        this.requestMapper = requestMapper;
        this.appointmentMapper = appointmentMapper;
        this.employeeMapper = employeeMapper;
        this.smsPort = smsPort;
        this.departmentScheduleRepository = departmentScheduleRepository;
        this.requestDocumentRepository = requestDocumentRepository;
        this.departmentScheduleMapper = departmentScheduleMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Employee getEmployeeByUsername(String username) {
        return employeeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Employee with username " + username + " not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public RequestDocument getDocument(Long documentId) {
        return requestDocumentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document with id " + documentId + " not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentScheduleView> getDepartmentScheduleForEmployee(Long employeeId) {
        return departmentScheduleRepository
                .findByDepartmentId(employeeRepository
                        .findById(employeeId)
                        .orElseThrow(() -> new RuntimeException("Employee with id " + employeeId + " not found"))
                        .getDepartment().getId())
                .stream()
                .map(departmentScheduleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeView> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        return employees
                .stream()
                .map(employeeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestView> getRequestsForEmployeeDepartment(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow();
        return requestRepository.findByDepartmentId(employee.getDepartment().getId())
                .stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestView> getRequestsForDepartment(Long departmentId) {
        return requestRepository
                .findByDepartmentId(departmentId)
                .stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeView> getEmployeesByDepartment(Long departmentId) {
        return employeeRepository.findByDepartmentId(departmentId)
                .stream()
                .map(employeeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateRequestProgress(Long requestId, Long employeeId, Request.Status newStatus, String comment) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Request.Status oldStatus = request.getStatus();

        // update the status if it's changed
        if (newStatus != null) {
            request.setStatus(newStatus);
        }

        // Log
        RequestLog log = new RequestLog();
        log.setActionDate(LocalDateTime.now());
        // if comment exists add it to the log else add a default message
        log.setComment((comment != null && !comment.isBlank()) ? comment : "Status updated to " + newStatus);
        log.setOldStatus(oldStatus);
        log.setNewStatus(request.getStatus());
        log.setRequest(request);
        log.setEmployee(employee);

        request.getLogs().add(log);
        requestRepository.save(request);

        if (newStatus == Request.Status.PENDING_DOCS) {
            smsPort.sendSms(request.getCitizen().getMobilePhoneNumber(),
                    "Your request status changed to: " + newStatus + ". " + comment);
        }

    }

    @Override
    @Transactional
    public RequestView getRequestById(Long id) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request with id " + id + " not found"));
        // fetch logs immediately to avoid LazyInitializationException in the controller/view
         request.getLogs().size();
        return requestMapper.toDto(request);
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
    @Transactional
    public void assignRequestToEmployee(Long requestId, Long employeeId) {
        Request request = requestRepository
                .findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request with id "+ requestId +"not found"));
        Employee employee = employeeRepository
                .findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee with id " + employeeId + " not found"));

        if (!request.getDepartment().getId().equals(employee.getDepartment().getId())) {
            throw new RuntimeException("Cannot assign request to an employee of a different department!");
        }
        Request.Status oldStatus = request.getStatus();
        request.setAssignedEmployee(employee);          // update request to include employee
        if (request.getStatus() == Request.Status.SUBMITTED) {
            request.setStatus(Request.Status.PROCESSING);
        }

        RequestLog log = new RequestLog();
        log.setActionDate(LocalDateTime.now());
        log.setComment("Assigned to: " + employee.getFirstName() + " " + employee.getLastName());
        log.setOldStatus(oldStatus);
        log.setNewStatus(Request.Status.PROCESSING);
        log.setRequest(request);
        log.setEmployee(employee); // or the current user if distinct from the assigned one

        request.getLogs().add(log);

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

        smsPort.sendSms(request.getCitizen().getMobilePhoneNumber(), "Your request has been rejected: " + reason);
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

        smsPort.sendSms(request.getCitizen().getMobilePhoneNumber(), "Your request has been accepted and completed");
    }

    @Override
    public List<AppointmentView> getAppointmentsForEmployeeDepartment(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow();
        return appointmentRepository.findByDepartmentId(employee.getDepartment().getId())
                .stream()
                .map(appointmentMapper::toAppointmentView)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void confirmAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow();
        appointment.setStatus(Appointment.AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);

        smsPort.sendSms(appointment.getCitizen().getMobilePhoneNumber(), "Your appointment has been confirmed for: " +  appointment.getAppointmentDate());
    }

    @Override
    @Transactional
    public void cancelAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow();
        appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
        smsPort.sendSms(appointment.getCitizen().getMobilePhoneNumber(), "Your Appointment has been cancelled");
    }

    @Override
    @Transactional
    public void rescheduleAppointment(Long appointmentId, LocalDateTime rescheduledDateTime) {
        Appointment appointment = appointmentRepository
                .findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment with id " + appointmentId + " not found"));

        // Date Validation
        if (rescheduledDateTime.getMinute() != 0 && rescheduledDateTime.getMinute() != 30) {
            throw new RuntimeException("Invalid time slot. Please choose XX:00 or XX:30.");
        }

        // Department Working Days
        DayOfWeek requestedDay = rescheduledDateTime.getDayOfWeek();
        DepartmentSchedule schedule = departmentScheduleRepository
                .findByDepartmentIdAndDayOfWeek(appointment.getDepartment().getId(), requestedDay)
                .orElseThrow(() -> new RuntimeException("The department is closed on " + requestedDay));

        LocalTime requestedTime = rescheduledDateTime.toLocalTime();
        if (requestedTime.isBefore(schedule.getStartTime()) || !requestedTime.isBefore(schedule.getEndTime())) {
            throw new RuntimeException("Selected time is outside working hours.");
        }

        boolean slotTaken = appointmentRepository.existsByDepartmentIdAndAppointmentDateAndStatusIn(
                appointment.getDepartment().getId(),
                rescheduledDateTime,
                List.of(Appointment.AppointmentStatus.SCHEDULED, Appointment.AppointmentStatus.COMPLETED)
        );

        if (slotTaken) {
            throw new RuntimeException("This appointment slot is already fully booked. Please choose another time.");
        }


        appointment.setAppointmentDate(rescheduledDateTime);
        appointment.setStatus(Appointment.AppointmentStatus.SCHEDULED); // reset status to SCHEDULED if it was rejected before

        appointmentRepository.save(appointment);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        smsPort.sendSms(appointment.getCitizen().getMobilePhoneNumber(), "Your Appointment changed date.\nNew Date:"+ rescheduledDateTime.format(formatter));

    }
}
