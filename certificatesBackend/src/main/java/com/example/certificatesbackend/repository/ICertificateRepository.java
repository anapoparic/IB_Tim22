package com.example.certificatesbackend.repository;

import com.example.certificatesbackend.domain.Admin;
import com.example.certificatesbackend.domain.Certificate;
import com.example.certificatesbackend.domain.CertificateRequest;
import com.example.certificatesbackend.domain.enums.Template;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ICertificateRepository extends JpaRepository<Certificate, Long> {
    List<Certificate> findAllByTemplate(Template template);

    List<Certificate> findAllByIssuerAlias(String issuerAlias);

    Certificate findById(Integer rootId);

}
