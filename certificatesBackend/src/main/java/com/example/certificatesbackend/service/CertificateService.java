package com.example.certificatesbackend.service;

import com.example.certificatesbackend.domain.Certificate;
import com.example.certificatesbackend.domain.CertificateRequest;
import com.example.certificatesbackend.repository.ICertificateRepository;
import com.example.certificatesbackend.service.interfaces.ServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class CertificateService implements ServiceInterface<Certificate> {

    @Autowired
    private ICertificateRepository repository;

    @Override
    public Collection<Certificate> getAll() {
        return repository.findAll();
    }

    @Override
    public Certificate getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public Certificate create(Certificate certificate) throws Exception {
        return repository.save(certificate);
    }

    @Override
    public void delete(Long id) throws Exception {
        Certificate cer = repository.findById(id)
                .orElseThrow(() -> new Exception("Certificate with given id doesn't exist"));
        cer.setActive(false);
        repository.save(cer);
    }
}
