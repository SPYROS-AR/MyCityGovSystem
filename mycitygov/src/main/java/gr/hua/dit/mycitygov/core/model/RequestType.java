package gr.hua.dit.mycitygov.core.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter @Getter
@Table(name = "request_types")
public class RequestType {

    @Id
    @Setter(AccessLevel.NONE) // Do not create setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    private RequestCategory requestCategory;


    @Column(name = "sla_days")
    private Integer slaDays;

    @Column(name = "required_documents")
    private String requiredDocuments;

    @Column(name = "is_active")
    private boolean isActive;

    // Department responsible for the request
    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    public RequestType() {
    }

    public RequestType(String name,
                       RequestCategory requestCategory,
                       Integer slaDays,
                       String requiredDocuments,
                       boolean isActive,
                       Department department) {
        this.name = name;
        this.requestCategory = requestCategory;
        this.slaDays = slaDays;
        this.requiredDocuments = requiredDocuments;
        this.isActive = isActive;
        this.department = department;
    }


    public enum RequestCategory {
        CERTIFICATE,
        PROBLEM,
        GENERAL
    }
}
