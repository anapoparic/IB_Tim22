package com.example.certificatesbackend.service;

import com.example.certificatesbackend.domain.Admin;
import com.example.certificatesbackend.repository.IAdminRepository;
import com.example.certificatesbackend.service.interfaces.ServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class AdminService implements ServiceInterface<Admin> {
    @Autowired
    private IAdminRepository repository;

    @Override
    public Collection<Admin> getAll() {
        return repository.findAllAdmins();
    }

    @Override
    public Admin getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public Admin create(Admin admin) throws Exception {
        if (admin.getId() != null) {
            throw new Exception("Id must be null when persisting a new entity.");
        }
        return repository.save(admin);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);

    }

}