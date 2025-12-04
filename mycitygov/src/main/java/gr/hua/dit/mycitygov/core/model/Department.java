package gr.hua.dit.mycitygov.core.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;


@Entity
@Setter @Getter
@Table(name = "departments")
public class Department {

    @Id
    @Setter(AccessLevel.NONE) // Do not create setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT") // Για να επιτρέπει μεγάλο κείμενο
    private String description;

    public Department() {
    }

    public Department( String name, String description) {
        this.name = name;
        this.description = description;
    }

}
