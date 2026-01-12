package gr.hua.dit.mycitygov.core.service;

import gr.hua.dit.mycitygov.core.model.*;
import gr.hua.dit.mycitygov.core.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
public class InitService implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(InitService.class);

    private final ClientRepository clientRepository;
    private final DepartmentRepository departmentRepository;
    private final DepartmentScheduleRepository departmentScheduleRepository;
    private final EmployeeRepository employeeRepository;
    private final CitizenRepository citizenRepository;
    private final RequestRepository requestRepository;
    private final RequestTypeRepository requestTypeRepository;
    private final AppointmentRepository appointmentRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public InitService(ClientRepository clientRepository,
                       DepartmentRepository departmentRepository,
                       DepartmentScheduleRepository departmentScheduleRepository,
                       EmployeeRepository employeeRepository,
                       CitizenRepository citizenRepository,
                       RequestRepository requestRepository,
                       RequestTypeRepository requestTypeRepository,
                       AppointmentRepository appointmentRepository,
                       AdminRepository adminRepository,
                       PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.departmentRepository = departmentRepository;
        this.departmentScheduleRepository = departmentScheduleRepository;
        this.employeeRepository = employeeRepository;
        this.citizenRepository = citizenRepository;
        this.requestRepository = requestRepository;
        this.requestTypeRepository = requestTypeRepository;
        this.appointmentRepository = appointmentRepository;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        logger.info("--- STARTING DATABASE INITIALIZATION ---");

        try {
            // 1. Client Entity
            initClients();

            // 2. Admin Entity (Single Object)
            initAdmin();

            // 3. Department Entity
            Department cleanliness = initDepartment("Cleanliness", "Waste management and recycling services");
            Department technical = initDepartment("Technical Services", "Infrastructure and urban planning");
            Department social = initDepartment("Social Services", "Social welfare and community support");

            // 4. DepartmentSchedule Entity
            initSchedules(List.of(cleanliness, technical, social));

            // 5. Employee Entity (More employees added for assignment testing)
            // Cleanliness Dept
            Employee emp1 = initEmployee("emp1", "Robert", "Miller", "r.miller@mycity.gov", cleanliness);
            initEmployee("emp4", "Alice", "Cooper", "a.cooper@mycity.gov", cleanliness); // Συνάδελφος του emp1

            // Technical Dept
            initEmployee("emp2", "Sarah", "Jenkins", "s.jenkins@mycity.gov", technical);
            initEmployee("emp5", "Bob", "Marley", "b.marley@mycity.gov", technical); // Συνάδελφος του emp2

            // Social Dept
            initEmployee("emp3", "David", "Wilson", "d.wilson@mycity.gov", social);
            initEmployee("emp6", "Charlie", "Chaplin", "c.chaplin@mycity.gov", social); // Συνάδελφος του emp3

            // 6. Citizen Entity
            List<Citizen> citizens = initCitizens();
            Citizen emily = citizens.get(0);

            // 7. RequestType Entity (ΠΡΟΣΟΧΗ: Προστέθηκαν οι ημέρες SLA στο τέλος)
            RequestType wasteType = initRequestType("WASTE_COLLECTION", RequestType.RequestCategory.PROBLEM, cleanliness, 5);
            RequestType road_repair = initRequestType("ROAD_REPAIR", RequestType.RequestCategory.PROBLEM, technical, 10);

            // 8. Request & 9. RequestLog Entities
            initRequests( "REQ-2025-001", cleanliness, emily, wasteType, emp1);
            initRequests("REQ-2025-002", technical  , emily, road_repair, emp1);
            // 10. Appointment Entity
            initAppointments(cleanliness, emily);

            logger.info("--- DATABASE INITIALIZATION FINISHED SUCCESSFULLY ---");
        } catch (Exception e) {
            logger.error("Error during database initialization: ", e);
        }
    }

    private void initClients() {
        if (clientRepository.findByName("my_app").isEmpty()) {
            clientRepository.save(new Client("my_app", passwordEncoder.encode("secret123"), "CLIENT_READ"));
            logger.info("Entity 'Client' initialized.");
        }
    }

    private void initAdmin() {
        if (adminRepository.count() == 0) {
            Admin admin = new Admin();
            admin.setFirstName("John");
            admin.setLastName("Admin");
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@mycity.gov");
            adminRepository.save(admin);
            logger.info("Entity 'Admin' initialized.");
        }
    }

    private Department initDepartment(String name, String description) {
        return departmentRepository.findByName(name)
                .orElseGet(() -> {
                    Department d = new Department();
                    d.setName(name);
                    d.setDescription(description);
                    logger.info("Department '{}' created.", name);
                    return departmentRepository.save(d);
                });
    }

    private void initSchedules(List<Department> departments) {
        for (Department dept : departments) {
            if (departmentScheduleRepository.findByDepartmentId(dept.getId()).isEmpty()) {
                for (DayOfWeek day : List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY)) {
                    departmentScheduleRepository.save(new DepartmentSchedule(dept, day, LocalTime.of(8, 0), LocalTime.of(15, 0)));
                }
                logger.info("Entity 'DepartmentSchedule' initialized for {}.", dept.getName());
            }
        }
    }

    private Employee initEmployee(String username, String first, String last, String email, Department dept) {
        Employee emp = employeeRepository.findByUsername(username).orElse(null);
        if (emp == null) {
            emp = new Employee();
            emp.setUsername(username);
            emp.setPassword(passwordEncoder.encode("password"));
            emp.setFirstName(first);
            emp.setLastName(last);
            emp.setEmail(email);
            emp.setDepartment(dept);
            emp = employeeRepository.save(emp);
            logger.info("Employee '{}' created.", username);
        }
        return emp;
    }

    private List<Citizen> initCitizens() {
        Citizen c1 = citizenRepository.findByEmail("emily.t@gmail.com").orElseGet(() -> {
            Citizen c = new Citizen();
            c.setUsername("citizen1");
            c.setPassword(passwordEncoder.encode("password"));
            c.setFirstName("Emily");
            c.setLastName("Thompson");
            c.setEmail("emily.t@gmail.com");
            c.setNationalId("ID123456");
            c.setMobilePhoneNumber("5550101");
            c.setAddress("10 Broadway Ave, City Center");
            return citizenRepository.save(c);
        });
        return List.of(c1);
    }

    private RequestType initRequestType(String name, RequestType.RequestCategory category, Department dept, Integer sla) {
        return requestTypeRepository.findByName(name).orElseGet(() -> {
            RequestType rt = new RequestType();
            rt.setName(name);
            rt.setRequestCategory(category);
            rt.setDepartment(dept);
            rt.setSlaDays(sla);
            rt.setActive(true);
            logger.info("RequestType '{}' created.", name);
            return requestTypeRepository.save(rt);
        });
    }

    private void initRequests(String protocolNumber, Department dept, Citizen citizen, RequestType type, Employee employee) {
        if (requestRepository.findByProtocolNumber(protocolNumber).isEmpty()) {
            Request req = new Request();
            req.setProtocolNumber(protocolNumber);
            req.setDescription("Pothole reported at Main Street.");
            req.setStatus(Request.Status.PROCESSING);
            req.setDepartment(dept);
            req.setCitizen(citizen);
            req.setRequestType(type);
            req.setAssignedEmployee(employee);
            if (requestRepository.count() == 0) { // First request is be overdue
                req.setSubmittedDate(LocalDateTime.now().minusMonths(1));
            }
            // Initialize RequestLog Entity
            RequestLog log = new RequestLog();
            log.setActionDate(LocalDateTime.now());
            log.setComment("Request assigned to employee for review.");
            log.setOldStatus(Request.Status.SUBMITTED);
            log.setNewStatus(Request.Status.PROCESSING);
            log.setEmployee(employee);
            log.setRequest(req);
            req.getLogs().add(log);

            requestRepository.save(req);
            logger.info("Entities 'Request' and 'RequestLog' initialized.");
        }
    }

    private void initAppointments(Department dept, Citizen citizen) {
        if (appointmentRepository.count() == 0) {
            Appointment app = new Appointment();
            app.setCitizen(citizen);
            app.setDepartment(dept);
            app.setAppointmentDate(LocalDateTime.now().plusDays(2)
                    .withHour(10).withMinute(0).withSecond(0).withNano(0));
            app.setStatus(Appointment.AppointmentStatus.SCHEDULED);
            appointmentRepository.save(app);
            logger.info("Entity 'Appointment' initialized.");
        }
    }
}