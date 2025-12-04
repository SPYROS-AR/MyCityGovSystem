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

* **Java Development Kit (JDK) 21** or newer
* **Maven** (Optional: You can use the included `mvnw` wrapper script instead)

## Tech Stack

This project is built using **Java 21** and **Spring Boot 3.5.8**.

### Core & Web

* **Spring Boot Starter Web:** Handles the web layer (Spring MVC) and includes an embedded Tomcat server.
* **Thymeleaf:** Server-side Java template engine for rendering HTML views.
* **Validation:** Implements Hibernate Validator for checking data integrity (e.g., `@NotNull`, `@Size`).

### Data & Persistence

* **Spring Data JPA:** Abstraction over Hibernate to interact with databases using Java Interfaces.
* **H2 Database:** An in-memory database used for development and testing environments.

### Security

* **Spring Security:** Handles authentication and authorization controls for the application.

### Developer Tools

* **Lombok:** Annotation library to reduce boilerplate code (Getters, Setters, Constructors).
* **Spring Boot DevTools:** Provides fast application restarts and LiveReload for an enhanced development experience.

### Testing

* **Spring Boot Starter Test:** Includes JUnit 5, Mockito, and AssertJ.
* **Spring Security Test:** Utilities for testing authentication flows.
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
