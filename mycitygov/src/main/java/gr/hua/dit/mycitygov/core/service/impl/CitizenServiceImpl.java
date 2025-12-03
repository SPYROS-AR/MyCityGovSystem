package gr.hua.dit.mycitygov.core.service.impl;

import gr.hua.dit.mycitygov.core.model.Citizen;
import gr.hua.dit.mycitygov.core.repository.CitizenRepository;
import gr.hua.dit.mycitygov.core.service.CitizenService;

import java.util.List;

public class CitizenServiceImpl implements CitizenService {

    private final CitizenRepository citizenRepository;

    public CitizenServiceImpl(CitizenRepository citizenRepository) {
        if(citizenRepository == null) throw new NullPointerException("citizenRepository cannot be null");
        this.citizenRepository = citizenRepository;
    }

    @Override
    public List<Citizen> getCitizens() {
        return citizenRepository.findAll();
    }

    /**
     * TODO: it should have CreateCitizenRequest as parameter and should return CreateCitizenResult
     * @param citizen
     * @return
     */
    @Override
    public Citizen createCitizen(Citizen citizen) {
        if(citizen == null) throw new NullPointerException("citizen cannot be null");

        // TODO from createCitizenRequest DTO do validations
        return citizenRepository.save(citizen); // for know just insert to the db
    }
}