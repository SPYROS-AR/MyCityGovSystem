package gr.hua.dit.mycitygov.core.service;

import gr.hua.dit.mycitygov.core.model.*;
import gr.hua.dit.mycitygov.core.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class InitService implements CommandLineRunner {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final CitizenRepository citizenRepository;
    private final RequestRepository requestRepository;
    private final RequestTypeRepository requestTypeRepository;
    private final AppointmentRepository appointmentRepository;
    private final AdminRepository adminRepository;

    public InitService(DepartmentRepository departmentRepository,
                       EmployeeRepository employeeRepository,
                       CitizenRepository citizenRepository,
                       RequestRepository requestRepository,
                       RequestTypeRepository requestTypeRepository,
                       AppointmentRepository appointmentRepository, AdminRepository adminRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
        this.citizenRepository = citizenRepository;
        this.requestRepository = requestRepository;
        this.requestTypeRepository = requestTypeRepository;
        this.appointmentRepository = appointmentRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    public void run(String... args) {
        System.out.println("--- STARTING DATABASE INITIALIZATION ---");

        // Department
        Department cleanlinessDept = departmentRepository.findByName("Cleanliness")
                .orElseGet(() -> {
                    Department d = new Department();
                    d.setName("Cleanliness");
                    d.setDescription("Υπηρεσίες Καθαριότητας και Ανακύκλωσης");
                    return departmentRepository.save(d);
                });

        // Employee
        Employee empGiannis = employeeRepository.findByEmail("emp1@city.gov");
        if (empGiannis == null) {
            Employee emp = new Employee();
            emp.setUsername("emp1");
            emp.setPassword("password");
            emp.setFirstName("Giannis");
            emp.setLastName("Employee");
            emp.setEmail("emp1@city.gov");
            emp.setDepartment(cleanlinessDept);
            empGiannis = employeeRepository.save(emp);
        }

        // Citizen
        Citizen citizenMaria = citizenRepository.findByEmail("cit@gmail.com").orElseGet(() -> {
            Citizen c = new Citizen();
            c.setUsername("citizen1");
            c.setPassword("password");
            c.setFirstName("Maria");
            c.setLastName("Citizen");
            c.setEmail("cit@gmail.com");
            c.setNationalId("123456789");
            c.setMobilePhoneNumber("6900123456");
            c.setAddress("Athens, Greece");
            return citizenRepository.save(c);
        });

        Citizen citizenNikos = citizenRepository.findByEmail("nikos@gmail.com").orElseGet(() -> {
            Citizen c = new Citizen();
            c.setUsername("nikos1");
            c.setPassword("password");
            c.setFirstName("Nikos");
            c.setLastName("Papadopoulos");
            c.setEmail("nikos@gmail.com");
            c.setNationalId("987654321");
            c.setMobilePhoneNumber("6900987654");
            c.setAddress("Thessaloniki, Greece");
            return citizenRepository.save(c);
        });

        // Request Type
        RequestType certType = requestTypeRepository.findByName("CERTIFICATE")
                .orElseGet(() -> {
                    RequestType rt = new RequestType();
                    rt.setName("CERTIFICATE");
                    rt.setRequestCategory(RequestType.RequestCategory.CERTIFICATE);
                    rt.setDepartment(cleanlinessDept);
                    return requestTypeRepository.save(rt);
                });

        RequestType garbageType = requestTypeRepository.findByName("BULK_WASTE")
                .orElseGet(() -> {
                    RequestType rt = new RequestType();
                    rt.setName("BULK_WASTE");
                    rt.setRequestCategory(RequestType.RequestCategory.PROBLEM);
                    rt.setDepartment(cleanlinessDept);
                    return requestTypeRepository.save(rt);
                });

        // Requests
        // SUBMITTED
        if (requestRepository.findByProtocolNumber("REQ-2025-001").isEmpty()) {
            Request req = new Request();
            req.setProtocolNumber("REQ-2025-001");
            req.setSubmittedDate(LocalDateTime.now().minusHours(2));
            req.setDescription("Υπάρχουν σκουπίδια στην πλατεία Ελευθερίας.");
            req.setStatus(Request.Status.SUBMITTED);
            req.setDepartment(cleanlinessDept);
            req.setCitizen(citizenMaria);
            req.setRequestType(certType);
            requestRepository.save(req);
        }

        // PROCESSING
        if (requestRepository.findByProtocolNumber("REQ-2025-002").isEmpty()) {
            Request req = new Request();
            req.setProtocolNumber("REQ-2025-002");
            req.setSubmittedDate(LocalDateTime.now().minusDays(1));
            req.setDescription("Παρακαλώ να μαζέψετε έναν παλιό καναπέ από την οδό Ερμού 12.");
            req.setStatus(Request.Status.PROCESSING);
            req.setDepartment(cleanlinessDept);
            req.setCitizen(citizenNikos);
            req.setRequestType(garbageType);
            req.setAssignedEmployee(empGiannis);

            // Log
            RequestLog log = new RequestLog();
            log.setActionDate(LocalDateTime.now().minusHours(5));
            log.setComment("Ανατέθηκε στον υπάλληλο Giannis Employee");
            log.setOldStatus(Request.Status.SUBMITTED);
            log.setNewStatus(Request.Status.PROCESSING);
            log.setEmployee(empGiannis);
            log.setRequest(req);
            req.getLogs().add(log);

            requestRepository.save(req);
        }

        // COMPLETED
        if (requestRepository.findByProtocolNumber("REQ-2025-003").isEmpty()) {
            Request req = new Request();
            req.setProtocolNumber("REQ-2025-003");
            req.setSubmittedDate(LocalDateTime.now().minusDays(5));
            req.setDescription("Καθαρισμός κάδου ανακύκλωσης.");
            req.setStatus(Request.Status.COMPLETED);
            req.setDepartment(cleanlinessDept);
            req.setCitizen(citizenMaria);
            req.setRequestType(certType);
            req.setAssignedEmployee(empGiannis);

            // Log 1: Assigned
            RequestLog log1 = new RequestLog();
            log1.setActionDate(LocalDateTime.now().minusDays(4));
            log1.setComment("Ανάθεση αιτήματος");
            log1.setOldStatus(Request.Status.SUBMITTED);
            log1.setNewStatus(Request.Status.PROCESSING);
            log1.setEmployee(empGiannis);
            log1.setRequest(req);
            req.getLogs().add(log1);

            // Log 2: COMPLETED
            RequestLog log2 = new RequestLog();
            log2.setActionDate(LocalDateTime.now().minusDays(1));
            log2.setComment("Ο κάδος καθαρίστηκε επιτυχώς.");
            log2.setOldStatus(Request.Status.PROCESSING);
            log2.setNewStatus(Request.Status.COMPLETED);
            log2.setEmployee(empGiannis);
            log2.setRequest(req);
            req.getLogs().add(log2);

            requestRepository.save(req);
        }

        // REJECTED
        if (requestRepository.findByProtocolNumber("REQ-2025-004").isEmpty()) {
            Request req = new Request();
            req.setProtocolNumber("REQ-2025-004");
            req.setSubmittedDate(LocalDateTime.now().minusDays(2));
            req.setDescription("Να κόψετε το δέντρο του γείτονα.");
            req.setStatus(Request.Status.REJECTED);
            req.setDepartment(cleanlinessDept);
            req.setCitizen(citizenNikos);
            req.setRequestType(garbageType);
            req.setAssignedEmployee(empGiannis);

            RequestLog log = new RequestLog();
            log.setActionDate(LocalDateTime.now());
            log.setComment("Απορρίφθηκε: Δεν είναι αρμοδιότητα του Δήμου (Ιδιωτικός χώρος).");
            log.setOldStatus(Request.Status.PROCESSING);
            log.setNewStatus(Request.Status.REJECTED);
            log.setEmployee(empGiannis);
            log.setRequest(req);
            req.getLogs().add(log);

            requestRepository.save(req);
        }

        // Appointments
        if (appointmentRepository.count() == 0) {
            // SCHEDULED
            Appointment app1 = new Appointment();
            app1.setCitizen(citizenMaria);
            app1.setDepartment(cleanlinessDept);
            app1.setAppointmentDate(LocalDateTime.now().plusDays(2).withHour(10).withMinute(0));
            app1.setStatus(Appointment.AppointmentStatus.SCHEDULED);
            appointmentRepository.save(app1);

            // COMPLETED
            Appointment app2 = new Appointment();
            app2.setCitizen(citizenNikos);
            app2.setDepartment(cleanlinessDept);
            app2.setAppointmentDate(LocalDateTime.now().plusDays(5).withHour(12).withMinute(30));
            app2.setStatus(Appointment.AppointmentStatus.COMPLETED);
            appointmentRepository.save(app2);

            // CANCELLED
            Appointment app3 = new Appointment();
            app3.setCitizen(citizenMaria);
            app3.setDepartment(cleanlinessDept);
            app3.setAppointmentDate(LocalDateTime.now().plusDays(1).withHour(0).withMinute(0)); // Dummy date
            app3.setStatus(Appointment.AppointmentStatus.CANCELLED);
            appointmentRepository.save(app3);

            System.out.println("Created Mock Appointments");

        }

        if (adminRepository.findByUsername("george123") == null) {
            Admin admin = new Admin();
            admin.setFirstName("Giorgos");
            admin.setLastName("Georgiou");
            admin.setPassword("george123");
            admin.setUsername("george123");
            admin.setEmail("giorgosgeorgiou123@gmail.com");
            adminRepository.save(admin);
        }

        System.out.println("--- DATABASE INITIALIZATION FINISHED ---");
    }
}