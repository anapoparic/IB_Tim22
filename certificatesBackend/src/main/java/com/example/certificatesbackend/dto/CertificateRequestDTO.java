package com.example.certificatesbackend.dto;

import jakarta.persistence.Column;

public class CertificateRequestDTO {

    private Integer id;
    private String commonName;
    private String givenName;
    private String surname;
    private String organization;
    private String email;
    private String uid;
}
