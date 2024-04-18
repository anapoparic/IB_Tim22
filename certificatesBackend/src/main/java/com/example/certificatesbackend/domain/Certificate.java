package com.example.certificatesbackend.domain;

import jakarta.persistence.*;

import java.util.Date;


@Entity
@Table(name = "certificate")
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private boolean isRevoked;

    @Column(nullable = false)
    private Date validStartDate;

    @Column(nullable = false)
    private Date validEndDate;

    @Column(nullable = false)
    private String issuerAlias;



}
