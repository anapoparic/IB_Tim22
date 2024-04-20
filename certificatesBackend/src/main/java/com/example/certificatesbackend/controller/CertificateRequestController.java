package com.example.certificatesbackend.controller;

import com.example.certificatesbackend.domain.CertificateRequest;
import com.example.certificatesbackend.dto.CertificateRequestDTO;
import com.example.certificatesbackend.mapper.CertificateRequestMapper;
import com.example.certificatesbackend.service.CertificateRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/requests")
@CrossOrigin
public class CertificateRequestController {

    @Autowired
    private CertificateRequestService service;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<CertificateRequestDTO>> getRequests() {
        Collection<CertificateRequest> requests = service.getAll();
        Collection<CertificateRequestDTO> requestDTOS = requests.stream()
                .map(CertificateRequestMapper::toDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(requestDTOS, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CertificateRequestDTO> getRequest(@PathVariable("id") Long id) {
        CertificateRequest request = service.getById(id);

        if (request == null) {
            return new ResponseEntity<CertificateRequestDTO>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<CertificateRequestDTO>(CertificateRequestMapper.toDto(request), HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CertificateRequestDTO> createRequest(@RequestBody CertificateRequestDTO requestDTO) throws Exception {
        CertificateRequest createdRequest = null;

        try {
            createdRequest = service.create(CertificateRequestMapper.toEntity(requestDTO));

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new CertificateRequestDTO(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(CertificateRequestMapper.toDto(createdRequest), HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<CertificateRequestDTO> deleteRequest(@PathVariable("id") Long id) {
        try {
            service.delete(id);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<CertificateRequestDTO>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(path="/active", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<CertificateRequestDTO>> getAllActiveRequests() {
        Collection<CertificateRequest> requests = service.getAllActiveRequests();
        Collection<CertificateRequestDTO> requestDTOS = requests.stream()
                .map(CertificateRequestMapper::toDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(requestDTOS, HttpStatus.OK);
    }

}
