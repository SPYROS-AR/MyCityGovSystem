# MyCityGov

---

###  Description

Digital Requests & Appointments Municipality Gate

---

### Available log-in roles:
- Citizen
- Employee (Case Handler)
- Admin

---

### Available services:

Citizens can:
- Submit digital requests to the Municipality (releases, licenses, certificates)
- Report problems in the city (potholes, lighting, cleanliness)
- Schedule appointments with municipal services (Citizens' Service Center, Technical Services, Social Services, etc.)

Municipality services can process all of the above with traceability and citizen notice

---

## Prerequisites

Before running the project, ensure you have the following installed:

* [Java Development Kit (JDK) 21](https://www.oracle.com/java/technologies/downloads/#java21)
* [Maven](https://maven.apache.org/download.cgi) (Optional: You can use the included `mvnw` wrapper script instead)

## Tech Stack

This project is built using [Java 21](https://docs.oracle.com/en/java/javase/21/) and [Spring Boot 3.5.8](https://docs.spring.io/spring-boot/3.5/).

### Core & Web

* [Spring Boot Starter Web](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-web): Handles the web layer ([Spring MVC](https://docs.spring.io/spring-framework/reference/web/webmvc.html)) and includes an embedded [Tomcat](https://tomcat.apache.org/) server.
* [Thymeleaf](https://www.thymeleaf.org/): Server-side Java template engine for rendering HTML views.
* Validation: Implements [Hibernate Validator](https://hibernate.org/validator/) for checking data integrity (e.g., `@NotNull`, `@Size`).

### Data & Persistence

* [Spring Data JPA](https://spring.io/projects/spring-data-jpa): Abstraction over Hibernate to interact with databases using Java Interfaces.
* [H2 Database](https://www.h2database.com/html/main.html): An in-memory database used for development and testing environments.

### Security

* [Spring Security](https://spring.io/projects/spring-security): Handles authentication and authorization controls for the application.

### Developer Tools

* [Lombok](https://projectlombok.org): Annotation library to reduce boilerplate code (Getters, Setters, Constructors).
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/reference/using/devtools.html): Provides fast application restarts and [LiveReload](https://livereload.net/) for an enhanced development experience.

### Testing

* [Spring Boot Starter Test](https://docs.spring.io/spring-boot/reference/testing/index.html): Includes [JUnit 5](https://junit.org), [Mockito](https://site.mockito.org), and [AssertJ](https://assertj.github.io/doc).
* [Spring Security Test](https://docs.spring.io/spring-security/reference/servlet/test/index.html): Utilities for testing authentication flows.
---

## Build & Run

Go to your desired directory, where you want the project to be installed:
```bash
cd $DESIRED_DIRECTORY
```

Clone repository from [GitFront](https://gitfront.io/):
```bash
git clone https://gitfront.io/r/spyros/kj1VBg62UHX4/mycitygov.git
```

Go to the project's directory:
```bash
cd mycitygov
```

Clean compile the project using Maven:
```bash
mvn clean package
```

Run with Maven Plugin:
```bash
# Windows
mvnw.cmd spring-boot:run

# macOS/Linux
./mvnw spring-boot:run
```

To access:
```bash
# Windows CMD
start http://localhost:8080

# Windows PowerShell
Start-Process http://localhost:8080

# macOS
open http://localhost:8080

# Linux (with xdg-open)
xdg-open http://localhost:8080
```
---

## DISCLAIMER
API Implementation is unfinished
