package rs.ac.uns.ftn.asd.BookedUp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.asd.BookedUp.dto.CertificateDTO;
import rs.ac.uns.ftn.asd.BookedUp.service.CertificateService;

@RestController
@RequestMapping("/api/certificates")
@CrossOrigin
public class CertificateController {

    @Autowired
    private CertificateService certificateService;


    @GetMapping(value = "/download/{alias}")
    public ResponseEntity<CertificateDTO> downloadPem(@PathVariable String alias) {
        return new ResponseEntity<>(this.certificateService.downloadCertificate(alias), HttpStatus.OK);
    }
}
