package com.example.certificatesbackend.dto;

import com.example.certificatesbackend.domain.enums.ReasonForRevoke;
import com.example.certificatesbackend.domain.enums.Template;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.ByteArrayInputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
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


    public CertificateDTO(Certificate certificate) {
        try {
            byte[] encoded = certificate.getEncoded();
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            X509Certificate x509Certificate = (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(encoded));
            this.issuerAlias = x509Certificate.getIssuerX500Principal().getName();
            this.alias = x509Certificate.getSubjectX500Principal().getName();
            this.validFrom = x509Certificate.getNotBefore();
            this.validTo = x509Certificate.getNotAfter();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
