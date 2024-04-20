package com.example.certificatesbackend.controller;

import com.example.certificatesbackend.domain.Certificate;
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

import java.util.Collection;
import java.util.stream.Collectors;

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

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CertificateDTO> createCertificate(@RequestBody CertificateRequestDTO requestDTO, String alias, String issuerAlias, String template) throws Exception {
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
}
