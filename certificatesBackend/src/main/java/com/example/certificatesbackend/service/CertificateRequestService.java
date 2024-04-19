package com.example.certificatesbackend.service;

import com.example.certificatesbackend.domain.CertificateRequest;
import com.example.certificatesbackend.repository.ICertificateRequestRepository;
import com.example.certificatesbackend.service.interfaces.ServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class CertificateRequestService implements ServiceInterface<CertificateRequest> {

    @Autowired
    public ICertificateRequestRepository repository;


    @Override
    public Collection<CertificateRequest> getAll() {
        return repository.findAll();
    }

    @Override
    public CertificateRequest getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public CertificateRequest create(CertificateRequest entity) throws Exception {
        if (entity.getId() != null){
            throw new Exception("Id must be null when persisting a new entity.");
        }
        return repository.save(entity);
    }

    @Override
    public CertificateRequest save(CertificateRequest entity) throws Exception {
        return repository.save(entity);
    }

    @Override
    public void delete(Long id) throws Exception {
        CertificateRequest req = repository.findById(id)
                .orElseThrow(() -> new Exception("Certification request with given id doesn't exist"));
        req.setActive(false);
        repository.save(req);
    }
}
