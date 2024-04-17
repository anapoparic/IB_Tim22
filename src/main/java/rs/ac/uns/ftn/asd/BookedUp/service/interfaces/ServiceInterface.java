package rs.ac.uns.ftn.asd.BookedUp.service.interfaces;

import java.util.Collection;
import java.util.List;

public interface ServiceInterface<T> {

    Collection<T> getAll();

    T getById(Long id);

    T create(T entity) throws Exception;

    T save(T entity) throws Exception;

    void delete(Long id) throws Exception;
}
