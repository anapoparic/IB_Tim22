package com.example.certificatesbackend.service.interfaces;

import java.util.Collection;

public interface ServiceInterface<T> {

    Collection<T> getAll();

    T getById(Long id);

    T create(T entity) throws Exception;

    void delete(Long id) throws Exception;
}