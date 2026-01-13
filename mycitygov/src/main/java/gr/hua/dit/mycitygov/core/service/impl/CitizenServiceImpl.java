package gr.hua.dit.mycitygov.core.service.impl;

import gr.hua.dit.mycitygov.core.model.*;
import gr.hua.dit.mycitygov.core.port.SmsNotificationPort;
import gr.hua.dit.mycitygov.core.repository.*;
import gr.hua.dit.mycitygov.core.service.CitizenService;
import gr.hua.dit.mycitygov.core.service.mapper.CitizenMapper;
import gr.hua.dit.mycitygov.core.service.mapper.DepartmentScheduleMapper;
import gr.hua.dit.mycitygov.core.service.model.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of the {@link CitizenService} interface.
 * <p>
 * This service handles the core business logic for Citizen operations, including:
 * <ul>
 * <li>Citizen registration and profile management.</li>
 * <li>Creation and retrieval of requests (applications).</li>
 * <li>Booking and retrieval of appointments with departments.</li>
 * </ul>
 */
@Service
public class CitizenServiceImpl implements CitizenService {

    private final CitizenRepository citizenRepository;
    private final CitizenMapper citizenMapper;
    private final PasswordEncoder passwordEncoder;
    private final RequestRepository requestRepository;
    private final RequestTypeRepository requestTypeRepository;
    private final AppointmentRepository appointmentRepository;
    private final DepartmentRepository departmentRepository;
    private final DepartmentScheduleRepository departmentScheduleRepository;
    private final DepartmentScheduleMapper departmentScheduleMapper;
    private final SmsNotificationPort smsPort;

    /**
     * Constructor for dependency injection.
     *
     * @param passwordEncoder       The encoder for securing citizen passwords.
     * @param citizenRepository     Repository for Citizen data access.
     * @param citizenMapper         Mapper for converting Citizen entities to DTOs.
     * @param requestRepository     Repository for Request data access.
     * @param requestTypeRepository Repository for RequestType data access.
     * @param appointmentRepository Repository for Appointment data access.
     * @param departmentRepository  Repository for Department data access.
     */
    public CitizenServiceImpl(PasswordEncoder passwordEncoder,
                              CitizenRepository citizenRepository,
                              CitizenMapper citizenMapper,
                              RequestRepository requestRepository,
                              RequestTypeRepository requestTypeRepository,
                              AppointmentRepository appointmentRepository,
                              DepartmentRepository departmentRepository,
                              DepartmentScheduleRepository departmentScheduleRepository,
                              DepartmentScheduleMapper departmentScheduleMapper,
                              SmsNotificationPort smsPort) {
        this.passwordEncoder = passwordEncoder;
        this.citizenRepository = citizenRepository;
        this.citizenMapper = citizenMapper;
        this.requestRepository = requestRepository;
        this.requestTypeRepository = requestTypeRepository;
        this.appointmentRepository = appointmentRepository;
        this.departmentRepository = departmentRepository;
        this.departmentScheduleRepository = departmentScheduleRepository;
        this.departmentScheduleMapper = departmentScheduleMapper;
        this.smsPort = smsPort;
    }

    /**
     * Retrieves a list of all registered citizens.
     *
     * @return A list of {@link Citizen} entities.
     */
    @Override
    public List<Citizen> getCitizens() { return citizenRepository.findAll(); }

    /**
     * Registers a new citizen in the system.
     * <p>
     * This method performs the following steps:
     * <ol>
     * <li>Validates the input (strips whitespace, checks for nulls).</li>
     * <li>Checks if the email, national ID, phone, or username already exists in the database.</li>
     * <li>Encodes the raw password.</li>
     * <li>Saves the new Citizen entity to the database.</li>
     * </ol>
     *
     * @param createCitizenRequest The DTO containing the registration details.
     * @return A {@link CreateCitizenResult} indicating success or failure (with a reason).
     * @throws NullPointerException if the request object is null.
     */
    @Override
    @Transactional
    public CreateCitizenResult createCitizen(final CreateCitizenRequest createCitizenRequest) {
        if (createCitizenRequest == null) throw new NullPointerException("citizen cannot be null");

        // Strip whitespace from input
        final String username = createCitizenRequest.username().strip();
        final String rawPassword = createCitizenRequest.rawPassword().strip();
        final String firstName = createCitizenRequest.firstName().strip();
        final String lastName = createCitizenRequest.lastName().strip();
        final String email = createCitizenRequest.email().strip();
        final String nationalId = createCitizenRequest.nationalId().strip();
        final String mobilePhoneNumber = createCitizenRequest.mobilePhoneNumber().strip();
        final String address = createCitizenRequest.address().strip();

        // ------------------------- Database Check --------------------------------------------
        // Check for existing records to ensure uniqueness
        if (this.citizenRepository.existsByEmailIgnoreCase(email))
            return CreateCitizenResult.failure("Email already exists");

        if (this.citizenRepository.existsByNationalIdIgnoreCase(nationalId))
            return CreateCitizenResult.failure("National ID already exists");

        if (this.citizenRepository.existsByMobilePhoneNumber(mobilePhoneNumber))
            return CreateCitizenResult.failure("Mobile Phone Number already exists");

        if (this.citizenRepository.existsByUsername(username))
            return CreateCitizenResult.failure("Username already exists");
        // -------------------------------------------------------------------------------------

        // Encode password
        String hashedPassword = passwordEncoder.encode(rawPassword);

        // Create a Citizen entity and save to DB
        Citizen citizen = new Citizen();
        citizen.setUsername(username);
        citizen.setPassword(hashedPassword);
        citizen.setFirstName(firstName);
        citizen.setLastName(lastName);
        citizen.setEmail(email);
        citizen.setNationalId(nationalId);
        citizen.setMobilePhoneNumber(mobilePhoneNumber);
        citizen.setAddress(address);

        citizen = citizenRepository.save(citizen);

        // Map Citizen to CitizenView
        CitizenView citizenView = this.citizenMapper.toDto(citizen);

        smsPort.sendSms(citizen.getMobilePhoneNumber(),
                "Welcome to MyCityGov, " + citizen.getFirstName() + "! Your account has been successfully created.");

        return CreateCitizenResult.success(citizenView);
    }

    /**
     * Retrieves a Citizen entity based on the provided username
     *
     * @param username the username of the citizen to be retrieved
     * @return the Citizen entity
     * @throws RuntimeException if no citizen is found with the given username
     */
    @Override
    public Citizen getCitizenByUsername(String username) {
        return citizenRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Citizen not found: " + username));
    }

    /**
     * Submits and saves a new request for a specific citizen.
     * <p>
     * This method handles the generation of a unique protocol number using a two-step save process:
     * <ol>
     * <li>The request is saved with a temporary protocol number to generate the database ID.</li>
     * <li>The request is updated with the final protocol number in the format {@code REQ-YYYY-ID}.</li>
     * </ol>
     * It also automatically routes the request to the department associated with the selected {@link RequestType}.
     *
     * @param dto       The DTO containing the request type ID and description.
     * @param citizenId The ID of the citizen submitting the request.
     * @return The saved {@link Request} entity.
     * @throws RuntimeException if the Citizen or RequestType is not found.
     */
    @Override
    @Transactional
    public Request saveRequest(SubmitRequestRequest dto, Long citizenId) {
        // Retrieve the citizen
        Citizen citizen = citizenRepository.findById(citizenId)
                .orElseThrow(() -> new RuntimeException("Citizen not found with ID: " + citizenId));

        // Retrieve the request type
        RequestType type = requestTypeRepository.findById(dto.requestTypeId())
                .orElseThrow(() -> new RuntimeException("Request Type not found with ID: " + dto.requestTypeId()));

        // Create and populate the Request entity
        Request request = new Request();
        request.setCitizen(citizen);
        request.setRequestType(type);
        // Automatically route the request to the department associated with the request type
        request.setDepartment(type.getDepartment());
        request.setDescription(dto.description());
        request.setSubmittedDate(LocalDateTime.now());
        request.setStatus(Request.Status.SUBMITTED);

        // Set a temporary protocol number (required by DB constraint)
        request.setProtocolNumber("TEMP-" + UUID.randomUUID());

        // Save the request to generate the ID
        request = requestRepository.save(request);

        // Generate the correct protocol number format: REQ-YYYY-ID
        String finalProtocolNumber = "REQ-" +
                request.getSubmittedDate().getYear() + "-" +
                String.format("%03d", request.getId());
        request.setProtocolNumber(finalProtocolNumber);

        // --- FILE ATTACHMENT LOGIC ---
        MultipartFile file = dto.attachment();
        if (file != null && !file.isEmpty()) {
            try {
                RequestDocument doc = new RequestDocument();
                doc.setFileName(file.getOriginalFilename());
                doc.setContentType(file.getContentType());
                doc.setData(file.getBytes());
                doc.setRequest(request);

                request.getDocuments().add(doc); // Add to relationship
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload file", e);
            }
        }

        Request savedRequest = requestRepository.save(request);

        // --- SMS NOTIFICATION: NEW REQUEST ---
        smsPort.sendSms(citizen.getMobilePhoneNumber(),
                "MyCityGov: Hello " + citizen.getFirstName() + ", your request has been submitted successfully. Protocol: " + finalProtocolNumber);

        // Update and save the request with the final protocol number
        return requestRepository.save(request);
    }

    /**
     * Retrieves the history of all requests submitted by a specific citizen.
     *
     * @param citizenId The ID of the citizen.
     * @return A list of {@link Request} entities belonging to the citizen.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Request> getMyRequests(Long citizenId) { return requestRepository.findByCitizenId(citizenId); }

    /**
     * Retrieves all available request types configured in the system.
     * Used to populate selection lists in the UI.
     *
     * @return A list of {@link RequestType} entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<RequestType> getAllRequestTypes() { return requestTypeRepository.findAll(); }

    /**
     * Books an appointment for a citizen with a specified department and appointment date
     * Validates the department schedule and ensures the requested appointment time falls within the department's working hours.
     *
     * @param dto the request object containing the appointment details such as department ID and appointment date
     * @param citizenId the unique identifier of the citizen booking the appointment
     * @return the saved {@link Appointment} object containing the details of the scheduled appointment
     * @throws RuntimeException if the citizen is not found, the department is not found, the department is closed on the requested day,
     *         or the requested time falls outside the department's working hours
     */
    @Override
    @Transactional
    public Appointment bookAppointment(BookAppointmentRequest dto, Long citizenId) {
        Citizen citizen = citizenRepository.findById(citizenId)
                .orElseThrow(() -> new RuntimeException("Citizen not found"));

        Department department = departmentRepository.findById(dto.departmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        LocalDateTime requestedDate = dto.appointmentDate().withSecond(0).withNano(0);

        if (requestedDate.getMinute() != 0 && requestedDate.getMinute() != 30) {
            throw new RuntimeException("Invalid time slot. Please choose XX:00 or XX:30.");
        }

        DayOfWeek requestedDay = requestedDate.getDayOfWeek();
        LocalTime requestedTime = requestedDate.toLocalTime();

        DepartmentSchedule scheduleEntity = departmentScheduleRepository
                .findByDepartmentIdAndDayOfWeek(dto.departmentId(), requestedDay)
                .orElseThrow(() -> new RuntimeException("The department is closed on " + requestedDay));

        DepartmentScheduleView scheduleView = departmentScheduleMapper.toDto(scheduleEntity);

        if (requestedTime.isBefore(scheduleView.startTime()) || !requestedTime.isBefore(scheduleView.endTime())) {
            throw new RuntimeException("Selected time is outside working hours.");
        }

        boolean slotTaken = appointmentRepository.existsByDepartmentIdAndAppointmentDateAndStatusIn(
                dto.departmentId(),
                requestedDate,
                List.of(Appointment.AppointmentStatus.SCHEDULED, Appointment.AppointmentStatus.COMPLETED)
        );

        if (slotTaken) {
            throw new RuntimeException("This appointment slot is already fully booked. Please choose another time.");
        }

        Appointment appointment = new Appointment();
        appointment.setCitizen(citizen);
        appointment.setDepartment(department);
        appointment.setAppointmentDate(requestedDate);
        appointment.setStatus(Appointment.AppointmentStatus.SCHEDULED);

        // --- SMS NOTIFICATION: APPOINTMENT ---
        String dateStr = requestedDate.toLocalDate().toString() + " at " + requestedDate.toLocalTime();
        smsPort.sendSms(citizen.getMobilePhoneNumber(),
                "MyCityGov: Hello " + citizen.getFirstName() + ", appointment booked with " + department.getName() + " on " + dateStr);

        return appointmentRepository.save(appointment);
    }

    /**
     * Retrieves all appointments (past and future) associated with a specific citizen.
     *
     * @param citizenId The ID of the citizen.
     * @return A list of {@link Appointment} entities.
     */
    @Override
    public List<Appointment> getMyAppointments(Long citizenId) { return appointmentRepository.findByCitizenId(citizenId); }

    /**
     * Retrieves a list of all departments in the municipality.
     * Used for selecting a department when booking an appointment.
     *
     * @return A list of {@link Department} entities.
     */
    @Override
    public List<Department> getAllDepartments() { return departmentRepository.findAll(); }
}