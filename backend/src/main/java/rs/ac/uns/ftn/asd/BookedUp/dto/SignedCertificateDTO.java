package rs.ac.uns.ftn.asd.BookedUp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SignedCertificateDTO {
    private String pemCertificate;
    private byte[] digitalSignature;
}
