package dk.cphbusiness.daos;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;

public interface IDAO<T> {
    T create(T t) throws Exception;
    T getById(String id) throws EntityNotFoundException;
    List<T> getAll();
    T update(T t) throws EntityNotFoundException;
    T delete(String id) throws EntityNotFoundException;
    List<T> findByProperty(String property, String propValue) throws EntityNotFoundException;
    boolean validateId(String id);
}
