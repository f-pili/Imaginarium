package it.fpili.imaginarium.service;

import it.fpili.imaginarium.exception.ApplicationException;
import it.fpili.imaginarium.factory.UserInputItemCreator;
import it.fpili.imaginarium.model.Item;
import it.fpili.imaginarium.persistence.Repository;
import it.fpili.imaginarium.util.InputSanitizer;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Application service exposing high-level catalog operations.
 * <p>
 * Responsibilities:
 * <ul>
 *     <li>Validate and sanitize user input before persisting.</li>
 *     <li>Delegate storage concerns to a {@link Repository}.</li>
 *     <li>Centralize business-oriented operations on {@link Item}.</li>
 * </ul>
 * This layer also demonstrates use of the Factory Method pattern via
 * {@link UserInputItemCreator}.
 * </p>
 */
public final class CatalogService {
    private final Repository<Item, String> repo;

    /**
     * Creates a new {@code CatalogService}.
     *
     * @param repo backing repository (must not be {@code null})
     * @throws NullPointerException if {@code repo} is {@code null}
     */
    public CatalogService(Repository<Item, String> repo) { // Catalog Service constructor
        this.repo = Objects.requireNonNull(repo, "repo must not be null");
    }

    /**
     * Creates or updates an item after sanitizing user-provided fields.
     * Uses a Factory to build the domain {@link Item}.
     *
     * @param id          unique identifier (max 40)
     * @param name        display name (max 80)
     * @param category    category label (max 80)
     * @param description short description (max 200)
     * @throws ApplicationException if persistence fails or validation rejects input
     */
    public void upsertItem(String id, String name, String category, String description) throws ApplicationException {
        String sid   = InputSanitizer.sanitizeLine(id, 40);
        String sname = InputSanitizer.sanitizeLine(name, 80);
        String scat  = InputSanitizer.sanitizeLine(category, 80);
        String sdesc = InputSanitizer.sanitizeLine(description, 200);
        repo.save(new UserInputItemCreator(sid, sname, scat, sdesc).build());
    }

    /**
     * Deletes an item by ID (sanitized defensively).
     *
     * @param id identifier to delete (max 40)
     * @throws ApplicationException if persistence fails or validation rejects input
     */
    public void deleteItem(String id) throws ApplicationException {
        String sid = InputSanitizer.sanitizeLine(id, 40);
        repo.deleteById(sid);
    }

    /**
     * Finds items whose name or category contains the given token (case-insensitive).
     *
     * @param token search token (max 80)
     * @return list of matching items (never {@code null})
     * @throws ApplicationException if repository access fails or validation rejects input
     */
    public List<Item> searchByToken(String token) throws ApplicationException {
        String t = InputSanitizer.sanitizeLine(token, 80).toLowerCase(); // Take input from user and sanitize it
        return repo.findAll().stream() // Ask the repo for all items
                .filter(i -> i.name().toLowerCase().contains(t) || i.category().toLowerCase().contains(t)) // For each item, filter by name or category to check if it matches the search token
                .collect(Collectors.toList()); // Collect results into a list
    }

    /**
     * Returns a snapshot of all items.
     *
     * @return immutable list of items (never {@code null})
     * @throws ApplicationException if repository access fails
     */
    public List<Item> findAll() throws ApplicationException {
        return repo.findAll();
    }
}