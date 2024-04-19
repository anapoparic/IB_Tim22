package com.example.certificatesbackend.repository;

import com.example.certificatesbackend.domain.Admin;
import com.example.certificatesbackend.domain.CertificateRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICertificateRequestRepository extends JpaRepository<CertificateRequest, Long> {
}
