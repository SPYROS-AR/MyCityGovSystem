package gr.hua.dit.mycitygov.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


@Entity
@Table(name="citizens" )
@PrimaryKeyJoinColumn(name = "id")
/**
 * Citizen entity for database with basic but immutable fields
 */
public class Citizen extends User {

    @NotBlank
    @Size(max = 9)
    @Column(unique = true)
    private String nationalId; //afm

    @NotBlank
    @Size(max = 18)
    private String mobilePhoneNumber;

    @NotBlank
    @Size(max = 100)
    private String address;


    public Citizen() {
        this.setRole(Role.CITIZEN);
    }

    public Citizen(String username, String password, String firstName, String lastName, String email, String nationalId, String mobilePhoneNumber, String address) {
        super(username, password, firstName, lastName, email);
        this.nationalId = nationalId;
        this.mobilePhoneNumber = mobilePhoneNumber;
        this.address = address;
        this.setRole(Role.CITIZEN);
    }


    public String getMobilePhoneNumber() {
        return mobilePhoneNumber;
    }

    public void setMobilePhoneNumber(String mobilePhoneNumber) {
        this.mobilePhoneNumber = mobilePhoneNumber;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
