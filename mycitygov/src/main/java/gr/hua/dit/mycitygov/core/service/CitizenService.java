package gr.hua.dit.mycitygov.core.service;

import gr.hua.dit.mycitygov.core.model.*;
import gr.hua.dit.mycitygov.core.service.model.BookAppointmentRequest;
import gr.hua.dit.mycitygov.core.service.model.CreateCitizenRequest;
import gr.hua.dit.mycitygov.core.service.model.CreateCitizenResult;
import gr.hua.dit.mycitygov.core.service.model.SubmitRequestRequest;

import java.util.List;

/**
 * Service interface defining the business logic for Citizen operations
 */
public interface CitizenService {
    /**
     * Retrieves all citizens in the system
     * @return List of Citizen entities
     */
    List<Citizen> getCitizens();

    /**
     * Registers a new citizen in the system
     * @param request The DTO containing registration details
     * @return The object indicating success or failure
     */
    CreateCitizenResult createCitizen(CreateCitizenRequest request);

    // --- Request Management Methods ---

    /**
     * Saves a new request submitted by a citizen.
     * @param dto The data transfer object containing the form data (type, description)
     * @param citizenId the ID of the citizen submitting the request
     * @return The saved Request entity
     */
    Request saveRequest(SubmitRequestRequest dto, Long citizenId);

    /**
     * Retrieves the history of requests for a specific citizen
     *
     * @param citizenId The ID of the citizen
     * @return List of all the citizen's requests
     */
    List<Request> getMyRequests(Long citizenId);

    /**
     * Retrieves all available request types
     * @return List of all RequestType entities
     */
    List<RequestType> getAllRequestTypes();

    /**
     * Books an appointment for a citizen with the specified department and date
     * @param dto The data transfer object
     * @param citizenId The ID of the citizen
     * @return The booked Appointment object
     */
    Appointment bookAppointment(BookAppointmentRequest dto, Long citizenId);

    /**
     * Retrieves the list of appointments for a specific citizen.
     * @param citizenId The ID of the citizen
     * @return List of Appointment entities
     */
    List<Appointment> getMyAppointments(Long citizenId);

    /**
     * Retrieves all available departments
     * @return List of Department entities
     */
    List<Department> getAllDepartments();
}