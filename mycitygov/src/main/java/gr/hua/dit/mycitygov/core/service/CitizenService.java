package gr.hua.dit.mycitygov.core.service;

import gr.hua.dit.mycitygov.core.model.Citizen;
import gr.hua.dit.mycitygov.core.service.model.CreateCitizenRequest;
import gr.hua.dit.mycitygov.core.service.model.CreateCitizenResult;

import java.util.List;

/**
 * TODO: 1. createCitizen should return CreateCitizenResult
 *       2. and getCitizens should return there DTO's
 */
public interface CitizenService {
    List<Citizen> getCitizens();
    CreateCitizenResult createCitizen(CreateCitizenRequest request);
}
