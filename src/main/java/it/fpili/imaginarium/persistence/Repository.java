package it.fpili.imaginarium.persistence;

import it.fpili.imaginarium.exception.ApplicationException;

import java.util.List;
import java.util.Optional;

/**
 * Minimal generic repository contract for CRUD-like access.
 *
 * <p>This abstraction allows decoupling of persistence logic
 * from business logic. Implementations may store entities in
 * memory, files, databases, or remote services.</p>
 *
 * @param <T>  entity type
 * @param <ID> identifier type (e.g. String, Long, UUID)
 */
public interface Repository<T, ID> {

    /**
     * Saves (inserts or updates) an entity.
     *
     * @param entity the entity to persist (non-null)
     * @throws ApplicationException if the persistence operation fails
     */
    void save(T entity) throws ApplicationException;

    /**
     * Finds an entity by its identifier.
     *
     * @param id the unique identifier (non-null)
     * @return an {@link Optional} containing the entity if found,
     *         or empty if not present
     * @throws ApplicationException if the retrieval fails
     */
    Optional<T> findById(ID id) throws ApplicationException;

    /**
     * Retrieves all entities in the repository.
     *
     * @return immutable list of all entities (never null, may be empty)
     * @throws ApplicationException if the retrieval fails
     */
    List<T> findAll() throws ApplicationException;

    /**
     * Deletes an entity by its identifier.
     *
     * @param id the identifier of the entity to delete
     * @throws ApplicationException if the deletion fails
     */
    void deleteById(ID id) throws ApplicationException;
}

