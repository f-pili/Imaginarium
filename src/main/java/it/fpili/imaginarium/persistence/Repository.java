package it.fpili.imaginarium.persistence;

import it.fpili.imaginarium.exception.ApplicationException;

import java.util.List;
import java.util.Optional;

/**
 * Minimal generic repository contract for CRUD-like access.
 * @param <T> entity type
 * @param <ID> identifier type
 */
public interface Repository<T, ID> {
    void save(T entity) throws ApplicationException;
    Optional<T> findById(ID id) throws ApplicationException;
    List<T> findAll() throws ApplicationException;
    void deleteById(ID id) throws ApplicationException;
}
