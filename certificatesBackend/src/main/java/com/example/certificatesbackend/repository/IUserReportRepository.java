package com.example.certificatesbackend.repository;

import com.example.certificatesbackend.domain.User;
import com.example.certificatesbackend.domain.UserReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface IUserReportRepository extends JpaRepository<UserReport, Long> {

    @Query("SELECT ur.reportedUser FROM UserReport ur WHERE ur.status = true AND ur.reportedUser.isBlocked = false" )
    Collection<User> findAllReportedUsers();

}
