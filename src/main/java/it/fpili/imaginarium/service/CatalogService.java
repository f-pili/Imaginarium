package it.fpili.imaginarium.service;

import it.fpili.imaginarium.exception.ApplicationException;
import it.fpili.imaginarium.model.Item;
import it.fpili.imaginarium.persistence.Repository;
import it.fpili.imaginarium.util.InputSanitizer;
import it.fpili.imaginarium.factory.UserInputItemCreator;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Application service exposing high-level catalog operations.
 */
public final class CatalogService {
    private final Repository<Item, String> repo;

    public CatalogService(Repository<Item, String> repo) {
        this.repo = Objects.requireNonNull(repo);
    }

    /**
     * Creates or updates an item after sanitizing user fields.
     */
    public void upsertItem(String id, String name, String category, String description) throws ApplicationException {
        String sid = InputSanitizer.sanitizeLine(id, 40);
        String sname = InputSanitizer.sanitizeLine(name, 80);
        String scat = InputSanitizer.sanitizeLine(category, 80);
        String sdesc = InputSanitizer.sanitizeLine(description, 200);
        repo.save(new UserInputItemCreator(sid, sname, scat, sdesc).build());
    }

    /**
     * Finds items whose name or category contains the given token (case-insensitive).
     */
    public List<Item> searchByToken(String token) throws ApplicationException {
        String t = InputSanitizer.sanitizeLine(token, 80).toLowerCase();
        return repo.findAll().stream()
                .filter(i -> i.name().toLowerCase().contains(t) || i.category().toLowerCase().contains(t))
                .collect(Collectors.toList());
    }

    /**
     * Returns a snapshot of all items.
     */
    public List<Item> findAll() throws ApplicationException {
        return repo.findAll();
    }
}
