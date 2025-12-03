package gr.hua.dit.mycitygov.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


@Entity
@PrimaryKeyJoinColumn(name = "user_id")
/**
 * Citizen entity for database with basic but immutable fields
 */
public class Citizen extends User {

    @NotBlank
    @Size(max = 9)
    @Column(unique = true)
    private String national_id; //afm

    @NotBlank
    @Size(max = 18)
    private String mobilePhoneNumber;

    @NotBlank
    @Email
    @Size(max = 100)
    @Column(unique = true)
    private String emailAddress;

    public Citizen() {
        this.setRole(Role.CITIZEN);
    }

    public Citizen(String username, String password, String firstName, String lastName, String email, String national_id, String mobilePhoneNumber, String emailAddress) {
        super(username, password, firstName, lastName, email);
        this.national_id = national_id;
        this.mobilePhoneNumber = mobilePhoneNumber;
        this.emailAddress = emailAddress;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getMobilePhoneNumber() {
        return mobilePhoneNumber;
    }

    public void setMobilePhoneNumber(String mobilePhoneNumber) {
        this.mobilePhoneNumber = mobilePhoneNumber;
    }

    public String getNational_id() {
        return national_id;
    }

    public void setNational_id(String national_id) {
        this.national_id = national_id;
    }
}
