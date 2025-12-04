package gr.hua.dit.mycitygov.core.repository;

import gr.hua.dit.mycitygov.core.model.Citizen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CitizenRepository extends JpaRepository<Citizen, Long> {

    Optional<Citizen> findByNationalId(String nationalId);

    Optional<Citizen> findByEmail(String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByNationalIdIgnoreCase(String nationalId);

    boolean existsByMobilePhoneNumberIgnoreCase(String mobilePhoneNumber);

    boolean existsByUsername(String username);

    boolean existsByMobilePhoneNumber(String mobilePhoneNumber);
}
