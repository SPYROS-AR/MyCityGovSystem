package gr.hua.dit.mycitygov.core.service.impl;

import gr.hua.dit.mycitygov.core.model.Citizen;
import gr.hua.dit.mycitygov.core.repository.CitizenRepository;
import gr.hua.dit.mycitygov.core.service.CitizenService;
import gr.hua.dit.mycitygov.core.service.mapper.CitizenMapper;
import gr.hua.dit.mycitygov.core.service.model.CitizenView;
import gr.hua.dit.mycitygov.core.service.model.CreateCitizenRequest;
import gr.hua.dit.mycitygov.core.service.model.CreateCitizenResult;


import java.util.List;

public class CitizenServiceImpl implements CitizenService {

    private final CitizenRepository citizenRepository;
    private final CitizenMapper citizenMapper;

    public CitizenServiceImpl(CitizenRepository citizenRepository, final  CitizenMapper citizenMapper) {
        if(citizenRepository == null) throw new NullPointerException("citizenRepository cannot be null");
        if(citizenMapper == null) throw new NullPointerException("citizenMapper cannot be null");
        this.citizenRepository = citizenRepository;
        this.citizenMapper = citizenMapper;
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
    public CreateCitizenResult createCitizen(final CreateCitizenRequest createCitizenRequest) {
        if(createCitizenRequest == null) throw new NullPointerException("citizen cannot be null");

        final String username = createCitizenRequest.username().strip();
        final String rawPassword = createCitizenRequest.rawPassword().strip();
        final String firstName = createCitizenRequest.firstName().strip();
        final String lastName = createCitizenRequest.lastName().strip();
        final String email = createCitizenRequest.email().strip();
        final String mationalId = createCitizenRequest.mationalId().strip();
        final String mobilePhoneNumber = createCitizenRequest.mobilePhoneNumber().strip();
        final String address = createCitizenRequest.address().strip();

        String hashedPassword = rawPassword; //TODO implemet encoding algorithm

        Citizen citizen = new Citizen();
        citizen.setUsername(username);
        citizen.setPassword(hashedPassword);
        citizen.setFirstName(firstName);
        citizen.setLastName(lastName);
        citizen.setEmail(email);
        citizen.setNationalId(mationalId);
        citizen.setMobilePhoneNumber(mobilePhoneNumber);
        citizen.setAddress(address);


        citizen = citizenRepository.save(citizen);


        CitizenView citizenView = this.citizenMapper.convertCitizenToCitizenView(citizen);
        // TODO from createCitizenRequest DTO do validations

        return CreateCitizenResult.success(citizenView);

    }
}