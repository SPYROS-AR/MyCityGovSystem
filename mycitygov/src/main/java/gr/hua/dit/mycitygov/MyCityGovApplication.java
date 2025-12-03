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

//@SpringBootApplication
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

			// 1. Create and Save a Department
			Department dept = new Department("Technical Service", "Handles roads and lights");
			departmentRepository.save(dept);
			System.out.println("Department saved: " + dept.getName());

			// 2. Create and Save an Employee linked to that Department
			Employee emp = new Employee("jdoe", "secret123", "John", "Doe", "john.doe@city.gov", dept);
			employeeRepository.save(emp);
			System.out.println("Employee saved: " + emp.getFirstName() + " (Dept: " + emp.getDepartment().getName() + ")");

			// 3. Create and Save a Citizen
			Citizen citizen = new Citizen("alice99", "pass123", "Alice", "Smith", "alice@gmail.com", "AFM123456", "6900123456");
			citizenRepository.save(citizen);
			System.out.println("Citizen saved: " + citizen.getFirstName() + " (AFM: " + citizen.getNational_id() + ")");

			// 4. Test Queries
			System.out.println("\n--- VERIFYING DATA ---");

			departmentRepository.findAll().forEach(d ->
					System.out.println("Found Dept: " + d.getName())
			);

			employeeRepository.findByDepartmentId(dept.getId()).forEach(e ->
					System.out.println("Found Employee in Technical Service: " + e.getLastName())
			);

			citizenRepository.findByEmail("alice@gmail.com").ifPresent(c ->
					System.out.println("Found Citizen by Email: " + c.getUsername())
			);

			System.out.println("--- TESTING FINISHED ---");
		};
	}

}
