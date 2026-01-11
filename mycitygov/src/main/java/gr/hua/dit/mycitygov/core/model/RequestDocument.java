package gr.hua.dit.mycitygov.core.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "request_documents")
@Getter @Setter
public class RequestDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String contentType;

    @Lob
    @Column(length = 10000000) // Increase size for larger files
    private byte[] data;

    @ManyToOne
    @JoinColumn(name = "request_id")
    private Request request;

    public RequestDocument() {}

    public RequestDocument(String fileName, String contentType, byte[] data, Request request) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.data = data;
        this.request = request;
    }
}