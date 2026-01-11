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

    public InitService(
            ClientRepository clientRepository,
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

        // 1. Initialize API Clients
        initClients();

        // 2. Initialize exactly one Admin
        initAdmin();

        // 3. Initialize Departments
        Department cleanliness = initDepartment("Cleanliness", "Waste management and recycling services");
        Department technical = initDepartment("Technical Services", "Infrastructure and urban planning");
        Department social = initDepartment("Social Services", "Social welfare and community support");

        // 4. Initialize Department Schedules (Mon-Fri 08:00 - 15:00)
        initSchedules(List.of(cleanliness, technical, social));

        // 5. Initialize at least 3 Employees
        initEmployees(cleanliness, technical, social);

        // Initialize Citizens and other data
        initCitizens();
        initRequestTypes(cleanliness, technical, social);

        logger.info("--- DATABASE INITIALIZATION FINISHED ---");
    }

    private void initClients() {
        if (clientRepository.findByName("my_app").isEmpty()) {
            clientRepository.save(new Client("my_app", passwordEncoder.encode("secret123"), "CLIENT_READ"));
            logger.info("Created Client: my_app");
        }
    }

    private void initAdmin() {
        // Ensure only one admin exists
        if (adminRepository.count() == 0) {
            Admin admin = new Admin();
            admin.setFirstName("John");
            admin.setLastName("Admin");
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@mycity.gov");
            adminRepository.save(admin);
            logger.info("Created single Admin: admin");
        }
    }

    private Department initDepartment(String name, String description) {
        return departmentRepository.findByName(name)
                .orElseGet(() -> {
                    Department d = new Department();
                    d.setName(name);
                    d.setDescription(description);
                    logger.info("Created Department: {}", name);
                    return departmentRepository.save(d);
                });
    }

    private void initSchedules(List<Department> departments) {
        for (Department dept : departments) {
            if (departmentScheduleRepository.findByDepartmentId(dept.getId()).isEmpty()) {
                // Working hours: Monday to Friday, 08:00 to 15:00
                for (DayOfWeek day : List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY)) {
                    departmentScheduleRepository.save(new DepartmentSchedule(dept, day, LocalTime.of(8, 0), LocalTime.of(15, 0)));
                }
                logger.info("Created schedules for Department: {}", dept.getName());
            }
        }
    }

    private void initEmployees(Department cleanliness, Department technical, Department social) {
        // Employee 1
        if (employeeRepository.findByUsername("emp1").isEmpty()) {
            createEmployee("emp1", "Robert", "Miller", "r.miller@mycity.gov", cleanliness);
        }
        // Employee 2
        if (employeeRepository.findByUsername("emp2").isEmpty()) {
            createEmployee("emp2", "Sarah", "Jenkins", "s.jenkins@mycity.gov", technical);
        }
        // Employee 3
        if (employeeRepository.findByUsername("emp3").isEmpty()) {
            createEmployee("emp3", "David", "Wilson", "d.wilson@mycity.gov", social);
        }
    }

    private void createEmployee(String username, String first, String last, String email, Department dept) {
        Employee emp = new Employee();
        emp.setUsername(username);
        emp.setPassword(passwordEncoder.encode("password"));
        emp.setFirstName(first);
        emp.setLastName(last);
        emp.setEmail(email);
        emp.setDepartment(dept);
        employeeRepository.save(emp);
        logger.info("Created Employee: {}", username);
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

        Citizen c2 = citizenRepository.findByEmail("mark.d@gmail.com").orElseGet(() -> {
            Citizen c = new Citizen();
            c.setUsername("citizen2");
            c.setPassword(passwordEncoder.encode("password"));
            c.setFirstName("Mark");
            c.setLastName("Davis");
            c.setEmail("mark.d@gmail.com");
            c.setNationalId("ID987654");
            c.setMobilePhoneNumber("5550202");
            c.setAddress("42 Sunset Blvd, Residential Area");
            return citizenRepository.save(c);
        });
        return List.of(c1, c2);
    }

    private void initRequestTypes(Department cleanliness, Department technical, Department social) {
        // Certificates and Problems for various departments
        createRequestType("WASTE_COLLECTION", RequestType.RequestCategory.PROBLEM, cleanliness);
        createRequestType("ROAD_REPAIR", RequestType.RequestCategory.PROBLEM, technical);
        createRequestType("SOCIAL_SUPPORT", RequestType.RequestCategory.CERTIFICATE, social);
    }

    private void createRequestType(String name, RequestType.RequestCategory category, Department dept) {
        if (requestTypeRepository.findByName(name).isEmpty()) {
            RequestType rt = new RequestType();
            rt.setName(name);
            rt.setRequestCategory(category);
            rt.setDepartment(dept);
            requestTypeRepository.save(rt);
            logger.info("Created Request Type: {}", name);
        }
    }
}