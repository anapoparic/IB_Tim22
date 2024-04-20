package com.example.certificatesbackend.service;

import com.example.certificatesbackend.domain.User;
import com.example.certificatesbackend.domain.UserReport;
import com.example.certificatesbackend.domain.enums.Role;
import com.example.certificatesbackend.dto.UserDTO;
import com.example.certificatesbackend.repository.IUserRepository;
import com.example.certificatesbackend.service.interfaces.ServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.certificatesbackend.service.UserReportService;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserService implements ServiceInterface<User> {
    @Autowired
    private IUserRepository repository;

    @Override
    public Collection<User> getAll() {
        Collection<User> users = repository.findAll();
        return users;
    }

    @Override
    public User getById(Long id) {
        User user = repository.findById(id).orElse(null);
        return user;
    }

    @Override
    public User create(User user) throws Exception {
        if (user.getId() != null) {
            throw new Exception("Id mora biti null prilikom perzistencije novog entiteta.");
        }
        user.setAndEncodePassword(user.getPassword());
        return repository.save(user);
    }

    @Override
    public User save(User user) {
        return repository.save(user);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    public boolean authenticateUser(String email, String password) {
        return true;
    }

    public boolean registerGuest(UserDTO userDTO) {
        //make sure to put user.role to guest
        return true;
    }

    public boolean registerHost(UserDTO userDTO) {
        //make sure to put user.role to host
        return false;
    }

    public User getByEmail(String email) {
        User user = repository.findByEmail(email).orElse(null);
        return user;
    }


    public Collection<User> getBlockedAll() {
        return repository.findAllBlockedUsers();
    }

    public Collection<User> getReportedAll() {
        Collection<User> users = repository.findAll();
        UserReportService reportService = new UserReportService();
        Collection<UserReport> reports = reportService.getAll();

        Collection<User> reportedUsers = users.stream()
                .filter(user -> reports.stream()
                        .anyMatch(report -> report.getReportedUser().getId().equals(user.getId()) && !report.isStatus()))
                .collect(Collectors.toList());

        return reportedUsers;
    }

    public Collection<User> getActiveAll() {
        Collection<User> users = repository.findAll();
        UserReportService reportService = new UserReportService();
        Collection<UserReport> reports = reportService.getAll();

        Collection<User> activeUsers = users.stream()
                .filter(user -> reports.stream()
                        .noneMatch(report -> report.getReportedUser().getId().equals(user.getId()) && report.isStatus()))
                .collect(Collectors.toList());

        return activeUsers;
    }

    public void blockUser(User user) {
        user.setBlocked(true);
        repository.save(user);
    }


    public void unblockUser(User user) {
        user.setBlocked(false);
        repository.save(user);
    }
}