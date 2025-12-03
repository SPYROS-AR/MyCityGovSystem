package gr.hua.dit.mycitygov.core.model;


import jakarta.persistence.*;

@Entity
@Table(name = "employees")
@PrimaryKeyJoinColumn(name = "user_id")
public class Employee extends User {

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;


    public Employee() {
        this.setRole(Role.EMPLOYEE);
    }

    public Employee(String username, String password, String firstName, String lastName, String email, Department department) {
        super(username, password, firstName, lastName, email);
        this.department = department;
        this.setRole(Role.EMPLOYEE);
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}
