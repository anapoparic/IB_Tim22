package com.example.certificatesbackend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SignedCertificateDTO {
    private String certificatePem;
    private byte[] digitalSignature;
}
