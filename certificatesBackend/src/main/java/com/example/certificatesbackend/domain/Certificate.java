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
    private String organizationUnit;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String ownerEmail;

    @Column(nullable = false)
    private boolean active;

    public Certificate(Date validFrom, Date validTo, String alias, String issuerAlias, boolean isRevoked, ReasonForRevoke reason, Template template, String commonName, String organization, String organizationUnit, String country, String ownerEmail, boolean active) {
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.alias = alias;
        this.issuerAlias = issuerAlias;
        this.isRevoked = isRevoked;
        this.reason = reason;
        this.template = template;
        this.commonName = commonName;
        this.organization = organization;
        this.organizationUnit = organizationUnit;
        this.country = country;
        this.ownerEmail = ownerEmail;
        this.active = active;
    }
}
