package gr.hua.dit.mycitygov.core.repository;

import gr.hua.dit.mycitygov.core.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    Admin findByUsername(String username);

    Admin findByEmail(String email);

}
