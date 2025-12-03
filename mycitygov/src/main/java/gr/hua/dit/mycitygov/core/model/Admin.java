package gr.hua.dit.mycitygov.core.model;


import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "admins")
@PrimaryKeyJoinColumn(name = "user_id")
public class Admin extends User{
    public Admin() {
        this.setRole(Role.ADMIN);
    }
    public Admin(String username, String password, String firstName, String lastName, String email, Department department) {
        super(username, password, firstName, lastName, email);
        this.setRole(Role.ADMIN);
    }
}
