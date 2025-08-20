package it.fpili.imaginarium;

import it.fpili.imaginarium.adapter.CsvRepositoryToJsonAdapter;
import it.fpili.imaginarium.composite.CatalogCategory;
import it.fpili.imaginarium.composite.CatalogComponent;
import it.fpili.imaginarium.composite.CatalogItem;
import it.fpili.imaginarium.exception.ApplicationException;
import it.fpili.imaginarium.exception.InputValidationException;
import it.fpili.imaginarium.iterator.CatalogItemCollection;
import it.fpili.imaginarium.iterator.ItemIterator;
import it.fpili.imaginarium.model.Item;
import it.fpili.imaginarium.persistence.CsvItemRepository;
import it.fpili.imaginarium.service.CatalogService;
import it.fpili.imaginarium.shielding.ExceptionShieldingHandler;
import it.fpili.imaginarium.util.InputSanitizer;
import it.fpili.imaginarium.util.LoggerConfig;
import it.fpili.imaginarium.util.SafeIO;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Command-line entry point for the Imaginarium application.
 *
 * <p>Responsibilities:
 * present a text menu, sanitize user input, delegate to services,
 * and shield user-facing errors via {@link ExceptionShieldingHandler}.</p>
 *
 * <p>This class is console-oriented and prints user-facing messages.</p>
 */
public final class Main {
    private static final Logger log = LoggerConfig.getLogger(Main.class);

    /**
     * Application entry point. Shows a looped menu and delegates to flows.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        log.info("Imaginarium CLI started");

        // Repository + Service wiring (relative data path; no hardcoded secrets).
        CatalogService service = new CatalogService(new CsvItemRepository(Path.of("data", "items.csv")));

        // Centralized Exception Shielding handler.
        ExceptionShieldingHandler shield = new ExceptionShieldingHandler(log);

        try (Scanner sc = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                printMenu();
                System.out.print("> ");
                String choice = sc.nextLine().trim();
                switch (choice) {
                    case "1" -> addItemFlow(sc, service, shield);
                    case "2" -> deleteItemFlow(sc, service, shield);
                    case "3" -> listItemsFlow(service, shield);
                    case "4" -> searchFlow(sc, service, shield);
                    case "5" -> printCategoryTreeFlow(service, shield);
                    case "6" -> iterateItemsFlow(service, shield);
                    case "7" -> exportJsonFlow(shield);
                    case "0" -> {
                        running = false;
                        System.out.println("Bye!");
                    }
                    default -> System.out.println("Unknown option. Please try again.");
                }
            }
        } catch (Exception ex) {
            // Last-resort guard for the CLI loop.
            log.log(Level.SEVERE, "Fatal error in CLI loop", ex);
            System.err.println("Unexpected error. Please check logs.");
            System.exit(2);
        }

        log.info("Imaginarium CLI stopped");
    }

    /** Prints the main menu. */
    private static void printMenu() {
        System.out.println();
        System.out.println("=== Imaginarium Catalog ===");
        System.out.println("1) Add/Update item");
        System.out.println("2) Delete item");
        System.out.println("3) List items");
        System.out.println("4) Search items");
        System.out.println("5) Show categories tree");
        System.out.println("6) Iterate items");
        System.out.println("7) Export catalog to JSON");
        System.out.println("0) Exit");
    }

    /**
     * Reads sanitized fields and upserts an item through the service under exception shielding.
     *
     * @param sc      console scanner
     * @param service catalog service
     * @param shield  shielding handler
     */
    private static void addItemFlow(Scanner sc, CatalogService service, ExceptionShieldingHandler shield) {
        try {
            System.out.print("Id (max 40): ");
            String id = InputSanitizer.sanitizeLine(sc.nextLine(), 40);

            System.out.print("Name (max 80): ");
            String name = InputSanitizer.sanitizeLine(sc.nextLine(), 80);

            System.out.print("Category (max 80): ");
            String cat = InputSanitizer.sanitizeLine(sc.nextLine(), 80);

            System.out.print("Description (max 200): ");
            String desc = InputSanitizer.sanitizeLine(sc.nextLine(), 200);

            shield.guard(() -> service.upsertItem(id, name, cat, desc),
                    "Could not save item. Please try again.");
            System.out.println("Saved.");
            log.info("Upserted item id=" + id);
        } catch (InputValidationException ive) {
            System.err.println("Validation error: " + ive.getMessage());
            log.log(Level.WARNING, "Validation failed during addItem", ive);
        } catch (ApplicationException ae) {
            System.err.println(ae.getMessage());
            log.log(Level.WARNING, "Application error during save", ae);
        }
    }

    /**
     * Handles the "delete item" flow: asks for an ID and deletes it.
     */
    private static void deleteItemFlow(Scanner sc, CatalogService service, ExceptionShieldingHandler shield) {
        try {
            System.out.print("Enter ID to delete (max 40): ");
            String id = InputSanitizer.sanitizeLine(sc.nextLine(), 40);

            shield.guard(() -> {
                service.deleteItem(id);
                return null;
            }, "Could not delete item. Please try again.");

            System.out.println("Deleted item with id=" + id);
            log.info("Deleted item id=" + id);
        } catch (InputValidationException ive) {
            System.err.println("Validation error: " + ive.getMessage());
            log.log(Level.WARNING, "Validation failed during deleteItem", ive);
        } catch (ApplicationException ae) {
            System.err.println(ae.getMessage());
            log.log(Level.WARNING, "Application error during delete", ae);
        }
    }

    /**
     * Lists all items using the service guarded by Exception Shielding.
     *
     * @param service catalog service
     * @param shield  shielding handler
     */
    private static void listItemsFlow(CatalogService service, ExceptionShieldingHandler shield) {
        try {
            List<Item> all = shield.guard(service::findAll, "Could not list items.");
            if (all.isEmpty()) {
                System.out.println("(no items)");
                return;
            }
            System.out.println("Items:");
            for (Item it : all) {
                System.out.println("- " + it.id() + " | " + it.name() + " | " + it.category() + " | " + it.description());
            }
        } catch (ApplicationException ae) {
            System.err.println(ae.getMessage());
        }
    }

    /**
     * Searches items by token with sanitization and shielding.
     *
     * @param sc      console scanner
     * @param service catalog service
     * @param shield  shielding handler
     */
    private static void searchFlow(Scanner sc, CatalogService service, ExceptionShieldingHandler shield) {
        try {
            System.out.print("Search token (max 80): ");
            String token = InputSanitizer.sanitizeLine(sc.nextLine(), 80);

            List<Item> found = shield.guard(() -> service.searchByToken(token),
                    "Could not search items.");

            if (found.isEmpty()) {
                System.out.println("(no matches)");
                return;
            }
            System.out.println("Matches:");
            for (Item it : found) {
                System.out.println("- " + it.id() + " | " + it.name() + " | " + it.category() + " | " + it.description());
            }
        } catch (InputValidationException ive) {
            System.err.println("Validation error: " + ive.getMessage());
            log.log(Level.WARNING, "Validation failed during search", ive);
        } catch (ApplicationException ae) {
            System.err.println(ae.getMessage());
        }
    }

    /**
     * Builds a Composite tree grouping items by category and prints it.
     *
     * @param service catalog service
     * @param shield  shielding handler
     */
    private static void printCategoryTreeFlow(CatalogService service, ExceptionShieldingHandler shield) {
        try {
            List<Item> all = shield.guard(service::findAll, "Could not load items.");
            if (all.isEmpty()) {
                System.out.println("(no items)");
                return;
            }
            CatalogCategory root = new CatalogCategory("Catalog");
            Map<String, CatalogCategory> categories = new LinkedHashMap<>();

            for (Item it : all) {
                String catName = it.category().isEmpty() ? "(uncategorized)" : it.category();
                CatalogCategory cat = categories.computeIfAbsent(catName, CatalogCategory::new);
                cat.addComponent(new CatalogItem(it));
            }
            for (CatalogComponent c : categories.values()) {
                root.addComponent(c);
            }
            root.showDetails();
        } catch (ApplicationException ae) {
            System.err.println(ae.getMessage());
            log.log(Level.WARNING, "Error printing category tree", ae);
        }
    }

    /**
     * Demonstrates custom iteration over items using our Iterator implementation.
     *
     * @param service catalog service
     * @param shield  shielding handler
     */
    private static void iterateItemsFlow(CatalogService service, ExceptionShieldingHandler shield) {
        try {
            List<Item> all = shield.guard(service::findAll, "Could not load items.");
            if (all.isEmpty()) {
                System.out.println("(no items)");
                return;
            }
            CatalogItemCollection col = new CatalogItemCollection();
            for (Item it : all) col.add(it);

            ItemIterator iter = col.createIterator();
            System.out.println("Iterating items via custom Iterator:");
            while (iter.hasNext()) {
                Item it = iter.next();
                System.out.println("- " + it.name());
            }
        } catch (ApplicationException ae) {
            System.err.println(ae.getMessage());
            log.log(Level.WARNING, "Iterator flow error", ae);
        }
    }

    /**
     * Exports the catalog to JSON through the Adapter and writes it to {@code data/items.json}.
     *
     * @param shield shielding handler
     */
    private static void exportJsonFlow(ExceptionShieldingHandler shield) {
        try {
            String json = shield.guard(() -> {
                var adapter = new CsvRepositoryToJsonAdapter(
                        new CsvItemRepository(Path.of("data", "items.csv"))
                );
                return adapter.toJson();
            }, "Could not export JSON.");

            SafeIO.writeUtf8(Path.of("data", "items.json"), json);
            System.out.println("Exported to data/items.json");
        } catch (ApplicationException ae) {
            System.err.println(ae.getMessage());
        }
    }
}