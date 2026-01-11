package gr.hua.dit.mycitygov.core.service.mapper;

import gr.hua.dit.mycitygov.core.model.Citizen;
import gr.hua.dit.mycitygov.core.service.model.CitizenView;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert {@link Citizen} to link {@link CitizenView}
 */

@Component
public class CitizenMapper {

    public CitizenView toDto(final Citizen citizen){
        if (citizen == null) return null;

        return new CitizenView(
                citizen.getId(),
                citizen.getUsername(),
                citizen.getFirstName(),
                citizen.getLastName(),
                citizen.getRole(),
                citizen.getEmail(),
                citizen.getNationalId(),
                citizen.getMobilePhoneNumber(),
                citizen.getAddress()
        );
    }
}
