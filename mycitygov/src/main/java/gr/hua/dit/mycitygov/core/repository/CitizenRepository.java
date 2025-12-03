package gr.hua.dit.mycitygov.core.repository;

import gr.hua.dit.mycitygov.core.model.Citizen;

import java.util.Optional;

public interface CitizenRepository {

    Optional<Citizen> findByNational_id(String national_id);

    Optional<Citizen> findByEmail(String email);
}
