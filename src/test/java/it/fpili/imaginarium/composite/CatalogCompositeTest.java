package it.fpili.imaginarium.composite;

import it.fpili.imaginarium.model.Item;
import org.junit.jupiter.api.Test;

class CatalogCompositeTest {

    @Test
    void demoComposite() {
        CatalogCategory root = new CatalogCategory("Root Catalog");

        CatalogCategory tools = new CatalogCategory("Tools");
        tools.addComponent(new CatalogItem(
                new Item("id-10","Sky Spoon","Tools","Scoops clouds")));
        tools.addComponent(new CatalogItem(
                new Item("id-11","Nimbus Brush","Tools","Paints on clouds")));

        CatalogCategory containers = new CatalogCategory("Containers");
        containers.addComponent(new CatalogItem(
                new Item("id-20","Echo Jar","Containers","Stores echoes")));

        root.addComponent(tools);
        root.addComponent(containers);

        root.showDetails();
    }
}
