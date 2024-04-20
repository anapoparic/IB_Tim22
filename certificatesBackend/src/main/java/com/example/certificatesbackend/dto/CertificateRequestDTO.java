package com.example.certificatesbackend.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CertificateRequestDTO {

    private Integer id;
    private String commonName;
    private String givenName;
    private String surname;
    private String organization;
    private String email;
    private String uid;
}
