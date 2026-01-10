/*
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
*/

package gr.hua.dit.mycitygov;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class }) // Απενεργοποίηση login για testing
public class MyCityGovApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyCityGovApplication.class, args);
    }

    /**
     * Επειδή το SecurityAutoConfiguration είναι excluded, πρέπει να φτιάξουμε
     * εμείς το Bean του PasswordEncoder, αλλιώς το CitizenService δεν μπορεί να ξεκινήσει.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Χρησιμοποιούμε τον NoOpPasswordEncoder που δεν κάνει κρυπτογράφηση (μόνο για development)
        return NoOpPasswordEncoder.getInstance();
    }
}