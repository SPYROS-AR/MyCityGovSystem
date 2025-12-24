# MyCityGov

---

### Description

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

### To Run the Application (using Docker)
* **[Docker Desktop](https://www.docker.com/products/docker-desktop/)**: The only requirement to build and run the entire system (App + Database + Tools). You do **not** need Java or Maven installed on your machine just to run it.

### To Develop the Application (using an IDE)
* **[Java Development Kit (JDK) 21](https://www.oracle.com/java/technologies/downloads/#java21)**: Required for your IDE (IntelliJ, Eclipse, VS Code) to compile code and provide intellisense.
* **[Maven](https://maven.apache.org/download.cgi)** (Optional): The project includes a Maven Wrapper (`mvnw`), so a local Maven installation is not strictly required.

---

## Tech Stack

This project is built using [Java 21](https://docs.oracle.com/en/java/javase/21/) and [Spring Boot 3.4.1](https://docs.spring.io/spring-boot/).

### Core & Web

* [Spring Boot Starter Web](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-web): Handles the web layer and includes embedded Tomcat.
* [Thymeleaf](https://www.thymeleaf.org/): Server-side Java template engine for rendering HTML views.
* Validation: Implements [Hibernate Validator](https://hibernate.org/validator/).

### Data & Persistence

* [Spring Data JPA](https://spring.io/projects/spring-data-jpa): Abstraction over Hibernate.
* **PostgreSQL**: Production-grade relational database (containerized).
* **Docker Compose**: Orchestrates the Application, Database, and pgAdmin containers.

### Security

* [Spring Security](https://spring.io/projects/spring-security): Handles authentication and authorization.

---

## Build & Run (Docker Compose)

The easiest and recommended way to run the application is using Docker Compose.

This project can be executed in two modes depending on your testing needs.
## Clone the repository
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

### Option A: Full Docker Execution (Recommended for Grading)
This method launches the entire stack (App + DB + Admin) in isolated containers, requiring no local Java setup.

1.  Open a terminal in the project root directory.
2.  Build and start the containers:
    ```bash
    docker-compose up --build
    ```
3.  Wait for the logs to indicate startup (`Started MyCityGovApplication in ... seconds`).
4.  Access the services:
    * **Web Application**: [http://localhost:8080](http://localhost:8080)
    * **pgAdmin 4**: [http://localhost:5050](http://localhost:5050)

### Option B: Hybrid Mode (Local Java + Docker DB)
Use this mode for development or debugging the Java code while keeping the database containerized.

1.  Start only the infrastructure containers:
    ```bash
    docker-compose up db pgadmin -d
    ```
2.  Run the Spring Boot application using the Maven wrapper:
    * **Windows**:
        ```cmd
        .\mvnw.cmd spring-boot:run
        ```
    * **Mac/Linux**:
        ```bash
        ./mvnw spring-boot:run
        ```

---




