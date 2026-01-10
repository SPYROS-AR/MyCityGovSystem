package gr.hua.dit.mycitygov.core.service.impl;

import gr.hua.dit.mycitygov.core.model.*;
import gr.hua.dit.mycitygov.core.repository.*;
import gr.hua.dit.mycitygov.core.service.CitizenService;
import gr.hua.dit.mycitygov.core.service.mapper.CitizenMapper;
import gr.hua.dit.mycitygov.core.service.model.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of the {@link CitizenService} interface.
 * Contains business logic for Citizen operations including profile, requests, and appointments.
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

    public CitizenServiceImpl(PasswordEncoder passwordEncoder,
                              CitizenRepository citizenRepository,
                              CitizenMapper citizenMapper,
                              RequestRepository requestRepository,
                              RequestTypeRepository requestTypeRepository,
                              AppointmentRepository appointmentRepository,
                              DepartmentRepository departmentRepository) {
        this.passwordEncoder = passwordEncoder;
        this.citizenRepository = citizenRepository;
        this.citizenMapper = citizenMapper;
        this.requestRepository = requestRepository;
        this.requestTypeRepository = requestTypeRepository;
        this.appointmentRepository = appointmentRepository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    public List<Citizen> getCitizens() {
        return citizenRepository.findAll();
    }

    @Override
    @Transactional
    public CreateCitizenResult createCitizen(final CreateCitizenRequest createCitizenRequest) {
        if(createCitizenRequest == null) throw new NullPointerException("citizen cannot be null");

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
            return CreateCitizenResult.failure("National Id already exists");

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
        CitizenView citizenView = this.citizenMapper.convertCitizenToCitizenView(citizen);

        return CreateCitizenResult.success(citizenView);
    }

    @Override
    @Transactional
    public Request saveRequest(SubmitRequestRequest dto, Long citizenId) {
        // 1. Retrieve the citizen
        Citizen citizen = citizenRepository.findById(citizenId)
                .orElseThrow(() -> new RuntimeException("Citizen not found with ID: " + citizenId));

        // 2. Retrieve the request type
        RequestType type = requestTypeRepository.findById(dto.requestTypeId())
                .orElseThrow(() -> new RuntimeException("Request Type not found with ID: " + dto.requestTypeId()));

        // 3. Create and populate the Request entity
        Request request = new Request();
        request.setCitizen(citizen);
        request.setRequestType(type);
        // Automatically route the request to the department associated with the request type
        request.setDepartment(type.getDepartment());
        request.setDescription(dto.description());
        request.setSubmittedDate(LocalDateTime.now());
        request.setStatus(Request.Status.SUBMITTED);

        // 4. Set a temporary protocol number (required by DB constraint)
        request.setProtocolNumber("TEMP-" + UUID.randomUUID());

        // 5. Save the request to generate the ID
        request = requestRepository.save(request);

        // 6. Generate the correct protocol number format: REQ-YYYY-ID
        String finalProtocolNumber = "REQ-" + request.getSubmittedDate().getYear() + "-" + request.getId();
        request.setProtocolNumber(finalProtocolNumber);

        // 7. Update and save the request with the final protocol number
        return requestRepository.save(request);
    }

    @Override
    public List<Request> getMyRequests(Long citizenId) {
        return requestRepository.findByCitizenId(citizenId);
    }

    @Override
    public List<RequestType> getAllRequestTypes() {
        return requestTypeRepository.findAll();
    }

    @Override
    @Transactional
    public Appointment bookAppointment(BookAppointmentRequest dto, Long citizenId) {
        Citizen citizen = citizenRepository.findById(citizenId)
                .orElseThrow(() -> new RuntimeException("Citizen not found"));

        Department department = departmentRepository.findById(dto.departmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        Appointment appointment = new Appointment();
        appointment.setCitizen(citizen);
        appointment.setDepartment(department);
        appointment.setAppointmentDate(dto.appointmentDate());
        appointment.setStatus(Appointment.AppointmentStatus.SCHEDULED);

        return appointmentRepository.save(appointment);
    }

    @Override
    public List<Appointment> getMyAppointments(Long citizenId) {
        return appointmentRepository.findByCitizenId(citizenId);
    }

    @Override
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }
}