package com.example.certificatesbackend.service;

import com.example.certificatesbackend.domain.User;
import com.example.certificatesbackend.dto.UserDTO;
import com.example.certificatesbackend.repository.IUserRepository;
import com.example.certificatesbackend.service.interfaces.ServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        System.out.println(user.getAddress().getId());
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

    public User getByEmail(String email) {
        User user = repository.findByEmail(email).orElse(null);
        return user;
    }

}