package com.example.certificatesbackend.repository;

import com.example.certificatesbackend.domain.Admin;
import com.example.certificatesbackend.domain.CertificateRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ICertificateRequestRepository extends JpaRepository<CertificateRequest, Long> {

    Optional<CertificateRequest> findByIdAndActive(Integer id, boolean b);

    List<CertificateRequest> findByActive(boolean b);
}
