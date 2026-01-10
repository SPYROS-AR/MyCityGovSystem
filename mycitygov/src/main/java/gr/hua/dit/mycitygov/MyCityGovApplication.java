package gr.hua.dit.mycitygov;

import gr.hua.dit.mycitygov.core.model.*;
import gr.hua.dit.mycitygov.core.repository.CitizenRepository;
import gr.hua.dit.mycitygov.core.repository.DepartmentRepository;
import gr.hua.dit.mycitygov.core.repository.EmployeeRepository;
import gr.hua.dit.mycitygov.core.repository.RequestRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class }) //TODO REMOVE
public class MyCityGovApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyCityGovApplication.class, args);
    }
}
