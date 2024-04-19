package com.example.certificatesbackend.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "requests")
public class CertificateRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
}
