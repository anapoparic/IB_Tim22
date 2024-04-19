package com.example.certificatesbackend.domain;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "requests")
public class CertificateRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String commonName;

    @Column(nullable = false)
    private String givenName;

    @Column(nullable = false)
    private String surname;

    @Column(nullable = false)
    private String organization;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String uid;

    @Column(nullable = false)
    private boolean active;
}
