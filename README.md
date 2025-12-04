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
- [Java Development Kit 21](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html) or [newer](https://www.oracle.com/java/technologies/downloads/)
- [Maven](https://maven.apache.org/download.cgi)

---

## Dependencies

- Backend Framework: [Spring Boot (v3.5.8)](https://spring.io/projects/spring-boot)
- Persistence: [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- Security: [Spring Security](https://spring.io/projects/spring-security)
- Frontend: [Thymeleaf](https://www.thymeleaf.org/)
- Database (Development): [H2 Database](https://www.h2database.com/html/main.html)

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
