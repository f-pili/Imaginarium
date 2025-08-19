package it.fpili.imaginarium.factory;

import it.fpili.imaginarium.model.Item;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ItemFactoryTest {

    @Test
    void userInputCreatorBuildsItem() {
        Item it = new UserInputItemCreator("id-7","Nimbus Brush","Tools","Paints on clouds").build();
        assertEquals("id-7", it.id());
        assertEquals("Nimbus Brush", it.name());
    }

    @Test
    void csvCreatorBuildsItem() {
        String[] cols = {"id-8","Whisper Box","Containers","Keeps secrets safe"};
        Item it = new CsvItemCreator(cols).build();
        assertEquals("id-8", it.id());
        assertEquals("Containers", it.category());
    }
}
