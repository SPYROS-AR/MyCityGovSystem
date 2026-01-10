package gr.hua.dit.mycitygov.core.service.impl;

import gr.hua.dit.mycitygov.core.model.*;
import gr.hua.dit.mycitygov.core.repository.*;
import gr.hua.dit.mycitygov.core.service.CitizenService;
import gr.hua.dit.mycitygov.core.service.mapper.CitizenMapper;
import gr.hua.dit.mycitygov.core.service.model.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
        if(citizenRepository == null) throw new NullPointerException("citizenRepository cannot be null");
        if(citizenMapper == null) throw new NullPointerException("citizenMapper cannot be null");
        if(requestRepository == null) throw new NullPointerException("requestRepository cannot be null");
        if(requestTypeRepository == null) throw new NullPointerException("requestTypeRepository cannot be null");
        if(appointmentRepository == null) throw new NullPointerException("appointmentRepository cannot be null");
        if(departmentRepository == null) throw new NullPointerException("departmentRepository cannot be null");
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

    /**
     * TODO: it should have CreateCitizenRequest as parameter and should return CreateCitizenResult
     *
     */
    @Override
    @Transactional
    public CreateCitizenResult createCitizen(final CreateCitizenRequest createCitizenRequest) {
        if(createCitizenRequest == null) throw new NullPointerException("citizen cannot be null");

        // Store CreatePersonRequest fields
        final String username = createCitizenRequest.username().strip();
        final String rawPassword = createCitizenRequest.rawPassword().strip();
        final String firstName = createCitizenRequest.firstName().strip();
        final String lastName = createCitizenRequest.lastName().strip();
        final String email = createCitizenRequest.email().strip();
        final String nationalId = createCitizenRequest.nationalId().strip();
        final String mobilePhoneNumber = createCitizenRequest.mobilePhoneNumber().strip();
        final String address = createCitizenRequest.address().strip();

        // ------------------------- Database Check --------------------------------------------
        // Check for existing records
        if (this.citizenRepository.existsByEmailIgnoreCase(email))
            return CreateCitizenResult.failure("Email already exists");

        if (this.citizenRepository.existsByNationalIdIgnoreCase(nationalId))
            return CreateCitizenResult.failure("National Id already exists");

        if (this.citizenRepository.existsByMobilePhoneNumber(mobilePhoneNumber))
            return CreateCitizenResult.failure("Mobile Phone Number already exists");

        if (this.citizenRepository.existsByUsername(username))
            return CreateCitizenResult.failure("Username already exists");

        // ------------------------- Database Check --------------------------------------------
        // Encode password
        String hashedPassword = passwordEncoder.encode(rawPassword);

        // Create a Citizen with the data of CreateCitizenRequest and insert to the DB
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
        // TODO from createCitizenRequest DTO do validations

        return CreateCitizenResult.success(citizenView);

    }

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

        // Set a temporary protocol number
        request.setProtocolNumber("TMP-" + UUID.randomUUID());

        // Save the request with the temporary protocol number to set its ID
        request = requestRepository.save(request);

        // Create the correct protocol number format
        String finalProtocolNumber = "REQ-" + request.getSubmittedDate().getYear() + "-" + request.getId();
        request.setProtocolNumber(finalProtocolNumber);

        // Save the request with the correct protocol number
        return requestRepository.save(request);
    }

    @Override
    public List<Request> getMyRequests(Long citizenId) {
        return List.of();
    }

    @Override
    public List<RequestType> getAllRequestTypes() {
        return List.of();
    }

    @Override
    public Appointment bookAppointment(BookAppointmentRequest dto, Long citizenId) { return null; }

    @Override
    public List<Appointment> getMyAppointments(Long citizenId) { return List.of(); }

    @Override
    public List<Department> getAllDepartments() { return List.of(); }
}