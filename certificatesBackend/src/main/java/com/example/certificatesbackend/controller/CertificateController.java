package com.example.certificatesbackend.controller;

import com.example.certificatesbackend.domain.Certificate;
import com.example.certificatesbackend.domain.CertificateRequest;
import com.example.certificatesbackend.domain.enums.ReasonForRevoke;
import com.example.certificatesbackend.dto.CertificateDTO;
import com.example.certificatesbackend.dto.CertificateRequestDTO;
import com.example.certificatesbackend.mapper.CertificateMapper;
import com.example.certificatesbackend.mapper.CertificateRequestMapper;
import com.example.certificatesbackend.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.certificatesbackend.constants.Constants.KEYSTORE_PASSWORD;
import static com.example.certificatesbackend.constants.Constants.KEYSTORE_PATH;

@RestController
@RequestMapping("/api/certificates")
@CrossOrigin
public class CertificateController {

    @Autowired
    private CertificateService service;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<CertificateDTO>> getCertificates() {
        Collection<Certificate> certificates = service.getAll();
        Collection<CertificateDTO> certificateDTOS = certificates.stream()
                .map(CertificateMapper::toDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(certificateDTOS, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CertificateDTO> getCertificate(@PathVariable("id") Long id) {
        Certificate certificate = service.getById(id);

        if (certificate == null) {
            return new ResponseEntity<CertificateDTO>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<CertificateDTO>(CertificateMapper.toDto(certificate), HttpStatus.OK);
    }

    @GetMapping(value = "/aliasByCommonName/{commonName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CertificateDTO> getAliasByCommonName(@PathVariable("commonName") String commonName) {
        Optional<Certificate> certificate = service.getAliasByCommonName(commonName);

        if (certificate.isEmpty()) {
            return new ResponseEntity<CertificateDTO>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<CertificateDTO>(CertificateMapper.toDto(certificate.get()), HttpStatus.OK);
    }

    @GetMapping(value = "/keyStore", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CertificateDTO>> getCertificatesFromKeyStore() {
        List<CertificateDTO> certifications = new ArrayList<>();
        List<java.security.cert.Certificate> certificates = service.getAllCertificates(KEYSTORE_PATH, KEYSTORE_PASSWORD);
        for (java.security.cert.Certificate certificate: certificates){
            certifications.add(new CertificateDTO(certificate));

        }
        return new ResponseEntity<List<CertificateDTO>>(certifications, HttpStatus.OK);
    }

    @PostMapping(value = "/generateRoot", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createRootCertificate(@RequestBody CertificateDTO certificateDTO, String uid) throws Exception {
        try {
            service.createRootCertificate(CertificateMapper.toEntity(certificateDTO), uid);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CertificateDTO> createCertificate(
            @RequestBody CertificateRequestDTO requestDTO,
            @RequestParam String alias,
            @RequestParam String issuerAlias,
            @RequestParam String template
    ) throws Exception {
        Certificate createdCertificate = null;

        try {
            createdCertificate = service.create(CertificateRequestMapper.toEntity(requestDTO), alias, issuerAlias, template);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new CertificateDTO(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(CertificateMapper.toDto(createdCertificate), HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Certificate> deleteCertificate(@PathVariable("id") Long id) {
        try {
            service.delete(id);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Certificate>(HttpStatus.NO_CONTENT);
    }

//    @PutMapping(value = "/revoke/{id}/{reason}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<String> revokeCertificate(@PathVariable Integer id, @PathVariable String reason) {
//        try {
//            service.revokeCertificate(id, ReasonForRevoke.valueOf(reason));
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//
//        return new ResponseEntity<>("OK", HttpStatus.OK);
//    }

    @CrossOrigin(origins = "https://localhost:4201")
    @RequestMapping(value = "/revoke/{id}/{reason}", method = RequestMethod.PUT)
    public ResponseEntity<String> revokeCertificate(@PathVariable Integer id, @PathVariable String reason) {
        try {
            service.revokeCertificate(id, ReasonForRevoke.valueOf(reason));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>( HttpStatus.OK);
    }
    @GetMapping(value = "/root", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<CertificateDTO>> getAllRootCertificates() {
        Collection<Certificate> certificates = service.getAllRoot();
        Collection<CertificateDTO> certificateDTOS = certificates.stream()
                .map(CertificateMapper::toDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(certificateDTOS, HttpStatus.OK);
    }

    @GetMapping(value = "/descendants/{rootId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<CertificateDTO>> getAllDescendantsOfRoot(@PathVariable Integer rootId) {
        Collection<Certificate> descendants = service.findAllChildren(rootId);

        Collection<CertificateDTO> descendantsDTOS = descendants.stream()
                .map(CertificateMapper::toDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(descendantsDTOS, HttpStatus.OK);
    }

    @GetMapping(value = "/pathToRoot/{rootId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<CertificateDTO>> getPathToRoot(@PathVariable Integer rootId) {
        Collection<Certificate> path = service.findPathToRoot(rootId);

        Collection<CertificateDTO> pathDTOS = path.stream()
                .map(CertificateMapper::toDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(pathDTOS, HttpStatus.OK);
    }
}
