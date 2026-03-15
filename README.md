# MyCityGov

> **Course Assignment:** Distributed Systems
> 
> **Topic:** Digital Municipality Gateway & Microservices Architecture

---

## About The Project

**MyCityGov** is a distributed platform connecting citizens with municipal services. It enables digital request submissions, issue reporting, and appointment scheduling. The system uses a microservices architecture, separating core business logic from notification handling.

---

## System Architecture

The system consists of three containerized components:

1.  **MyCityGov Core**: A Spring Boot application handling the web UI, authentication, and main business logic.
2.  **CitySmsService**: A standalone microservice dedicated to asynchronous SMS notifications.
3.  **Database**: A shared PostgreSQL instance for persistent storage.

---

## Key Features

### For Citizens
* **Profiles**: Register and manage personal details.
* **Requests**: Apply for certificates or report city issues (e.g., potholes).
* **Appointments**: Book slots with specific departments.
* **Tracking**: Monitor the status of requests in real-time.

### For Employees
* **Dashboard**: View assigned tasks and daily schedules.
* **Processing**: Approve, reject, or update the status of citizen requests.
* **Scheduling**: Manage and reschedule appointments.

### For Administrators
* **Analytics**: Monitor system usage and department performance.
* **Configuration**: Manage departments, operating hours, and service types.
* **Users**: Oversee all system accounts and roles.

---

## Tech Stack

* **Language**: Java 21
* **Framework**: Spring Boot 3.4
* **Database**: PostgreSQL
* **Frontend**: Thymeleaf & Bootstrap 5
* **Security**: Spring Security & JWT (via jjwt)
* **Documentation**: SpringDoc OpenAPI (Swagger UI)
* **Utilities**: Project Lombok
* **Infrastructure**: Docker & Docker Compose

---

## Deployment

Run the full system (App, Microservice, Database) with Docker Compose.

**Prerequisites:** Docker Desktop installed.

**How to Run:**
1. Clone the repository in the desired location using:
    ```bash
    git clone https://github.com/SPYROS-AR/MyCityGovSystem.git
    ```
2. Go inside the project root using:
   ```bash
   cd MyCityGovSystem
   ```    
3.  Run the command:
    ```bash
    docker-compose up --build -d
    ```
4.  Wait for all the containers to start

---

## Access Points

| Service | URL |
| :--- | :--- |
| **Web App** | `http://localhost:8080/login` |
| **Core API Docs** | `http://localhost:8080/swagger-ui/index.html` |
| **SMS Service API** | `http://localhost:8081/swagger-ui.html` |

---

## Default Credentials

| Login as: | Username | Password |
| :--- | :--- | :--- |
| **Admin** | `admin` | `p` |
| **Citizen** | `citizen1` | `p` |
| **Employee** | `emp1` | `p` |
| **Client** | `my_app` | `secret` |
