package com.example.certificatesbackend.dto;

import com.example.certificatesbackend.domain.enums.ReasonForRevoke;
import com.example.certificatesbackend.domain.enums.Template;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CertificateDTO {
    private Integer id;
    private Date validFrom;
    private Date validTo;
    private String alias;
    private String issuerAlias;
    private boolean isRevoked;
    private ReasonForRevoke reason;
    private Template template;
    private String commonName;
    private String organization;
}
