package com.example.certificatesbackend.domain;

import com.example.certificatesbackend.domain.enums.ReasonForRevoke;
import com.example.certificatesbackend.domain.enums.Template;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "certificate")
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Date validFrom;

    @Column(nullable = false)
    private Date validTo;

    @Column(nullable = false)
    private String alias;

    @Column(nullable = false)
    private String issuerAlias;

    @Column(nullable = false)
    private boolean isRevoked;

    @Column(nullable = true)
    private ReasonForRevoke reason;

    @Column(nullable = false)
    private Template template;

    @Column(nullable = false)
    private String commonName;

    @Column(nullable = false)
    private String organization;

    @Column(nullable = false)
    private String issuerEmail;

    @Column(nullable = false)
    private boolean active;

}
