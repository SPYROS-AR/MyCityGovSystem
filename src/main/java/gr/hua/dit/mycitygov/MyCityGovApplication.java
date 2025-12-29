package gr.hua.dit.mycitygov;

import gr.hua.dit.mycitygov.core.model.Citizen;
import gr.hua.dit.mycitygov.core.model.Department;
import gr.hua.dit.mycitygov.core.model.Employee;
import gr.hua.dit.mycitygov.core.repository.CitizenRepository;
import gr.hua.dit.mycitygov.core.repository.DepartmentRepository;
import gr.hua.dit.mycitygov.core.repository.EmployeeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class }) //TODO REMOVE
public class MyCityGovApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyCityGovApplication.class, args);
    }

    @Bean
    public CommandLineRunner testDatabase(DepartmentRepository departmentRepository,
                                          EmployeeRepository employeeRepository,
                                          CitizenRepository citizenRepository) {
        return args -> {
            System.out.println("--- STARTED TESTING DATABASE ---");

            // 1. Create Department if not exists
            Department dept = departmentRepository.findByName("Technical Service")
                    .orElseGet(() -> {
                        Department newDept = new Department("Technical Service", "Handles roads and lights");
                        return departmentRepository.save(newDept);
                    });
            System.out.println("Department: " + dept.getName());

            // 2. Create Employee if not exists
            if (employeeRepository.findByDepartmentId(dept.getId()).isEmpty()) {
                try {
                    Employee emp = new Employee("jdoe", "secret123", "John", "Doe", "john.doe@city.gov", dept);
                    employeeRepository.save(emp);
                    System.out.println("Employee saved: " + emp.getFirstName());
                } catch (Exception e) {
                    System.out.println("Employee jdoe probably already exists.");
                }
            } else {
                System.out.println("Employees for Technical Service already exist.");
            }

            // 3. Create Citizen if not exists
            if (citizenRepository.findByEmail("alice@gmail.com").isEmpty()) {
                Citizen citizen = new Citizen("alice99", "pass123", "Alice", "Smith", "alice@gmail.com", "AFM123456", "6900123456", "spiti");
                citizenRepository.save(citizen);
                System.out.println("Citizen saved: " + citizen.getFirstName());
            } else {
                System.out.println("Citizen alice already exists.");
            }

            System.out.println("--- TESTING FINISHED ---");
        };
    }
}
