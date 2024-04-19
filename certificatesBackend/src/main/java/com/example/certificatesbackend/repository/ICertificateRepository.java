package com.example.certificatesbackend.repository;

import com.example.certificatesbackend.domain.Admin;
import com.example.certificatesbackend.domain.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICertificateRepository extends JpaRepository<Certificate, Long> {
}
