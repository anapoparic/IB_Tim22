package com.example.certificatesbackend.repository;

import com.example.certificatesbackend.domain.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IAdminRepository extends JpaRepository<Admin, Long> {

    @Query(value = "SELECT * FROM USERS WHERE TYPE = 'admin'", nativeQuery = true)
    List<Admin> findAllAdmins();
}
