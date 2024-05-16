package com.example.certificatesbackend.repository;

import com.example.certificatesbackend.domain.Admin;
import com.example.certificatesbackend.domain.Certificate;
import com.example.certificatesbackend.domain.CertificateRequest;
import com.example.certificatesbackend.domain.enums.Template;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ICertificateRepository extends JpaRepository<Certificate, Long> {
    List<Certificate> findAllByTemplateAndActive(Template template, boolean b);

    List<Certificate> findByActive(boolean b);

    List<Certificate> findAllByIssuerAlias(String issuerAlias);

    Certificate findById(Integer rootId);

    Optional<Certificate> findByAlias(String issuerAlias);

    Optional<Certificate>  findByCommonName(String commonName);
    Optional<Certificate> findByOwnerEmailAndActive(String email, boolean b);
}
