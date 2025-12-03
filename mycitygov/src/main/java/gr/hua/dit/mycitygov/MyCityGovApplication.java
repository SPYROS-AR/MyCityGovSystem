package gr.hua.dit.mycitygov;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

//@SpringBootApplication
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class }) //TODO REMOVE
public class MyCityGovApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyCityGovApplication.class, args);
	}

}
