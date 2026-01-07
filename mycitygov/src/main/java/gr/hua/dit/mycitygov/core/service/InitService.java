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

    public InitService(DepartmentRepository departmentRepository,
                       EmployeeRepository employeeRepository,
                       CitizenRepository citizenRepository,
                       RequestRepository requestRepository,
                       RequestTypeRepository requestTypeRepository,
                       AppointmentRepository appointmentRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
        this.citizenRepository = citizenRepository;
        this.requestRepository = requestRepository;
        this.requestTypeRepository = requestTypeRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("--- STARTING DATABASE INITIALIZATION ---");

        // 1. Τμήμα (Cleanliness)
        Department cleanlinessDept = departmentRepository.findByName("Cleanliness")
                .orElseGet(() -> {
                    Department d = new Department();
                    d.setName("Cleanliness");
                    d.setDescription("Cleaning Services");
                    return departmentRepository.save(d);
                });

        // 2. Υπάλληλος (Giannis)
        if (employeeRepository.findByDepartmentId(cleanlinessDept.getId()).isEmpty()) {
            Employee emp = new Employee();
            emp.setUsername("emp1");
            emp.setPassword("password");
            emp.setFirstName("Giannis");
            emp.setLastName("Employee");
            emp.setEmail("emp1@city.gov");
            emp.setDepartment(cleanlinessDept);
            employeeRepository.save(emp);
        }

        // 3. Πολίτης (Maria)
        Citizen citizen = citizenRepository.findByEmail("cit@gmail.com").orElseGet(() -> {
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

        // 4. Τύπος Αιτήματος (CERTIFICATE)
        RequestType certType = requestTypeRepository.findByName("CERTIFICATE")
                .orElseGet(() -> {
                    RequestType rt = new RequestType();
                    rt.setName("CERTIFICATE");
                    rt.setRequestCategory(RequestType.RequestCategory.CERTIFICATE);
                    // --- Η ΔΙΟΡΘΩΣΗ ΕΙΝΑΙ ΕΔΩ ---
                    rt.setDepartment(cleanlinessDept); // Συνδέουμε τον τύπο με το τμήμα
                    // -----------------------------
                    return requestTypeRepository.save(rt);
                });

        // 5. Αίτηση (Request)
        if (requestRepository.findByDepartmentId(cleanlinessDept.getId()).isEmpty()) {
            Request req = new Request();
            req.setProtocolNumber("REQ-2025-001");
            req.setSubmittedDate(LocalDateTime.now());
            req.setDescription("Υπάρχουν σκουπίδια στην πλατεία.");
            req.setStatus(Request.Status.SUBMITTED);

            req.setDepartment(cleanlinessDept);
            req.setCitizen(citizen);
            req.setRequestType(certType);

            requestRepository.save(req);
            System.out.println("Created Mock Request for Cleanliness department");
        }

        if (appointmentRepository.count() == 0) {
            Appointment app1 = new Appointment();
            app1.setCitizen(citizen);
            app1.setDepartment(cleanlinessDept);
            app1.setAppointmentDate(LocalDateTime.now().plusDays(2).withHour(10).withMinute(0));
            app1.setStatus(Appointment.AppointmentStatus.SCHEDULED);
            appointmentRepository.save(app1);
            System.out.println("Created Mock Appointment 1");
        }

        System.out.println("--- DATABASE INITIALIZATION FINISHED ---");
    }
}