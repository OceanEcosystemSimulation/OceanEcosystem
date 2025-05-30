package extendedMechanics;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.List;

class DrawningAnimalsByTheirRarityTest {

    @Test
    void testRareRarity() {
        DrawningAnimalsByTheirRarity drawer = new DrawningAnimalsByTheirRarity(); //tworzenie instancji
        String rareAnimal = drawer.drawnAnimalByRarity(Rarity.RARE); //losowanie zwierzęcia z RARE
        assertTrue(List.of("Octopus","Nemo", "Dolphin",  "Star").contains(rareAnimal)); //czy zawiera
    }

    @Test
    void testSuperRareRarity() {
        DrawningAnimalsByTheirRarity drawer = new DrawningAnimalsByTheirRarity();
        String superRareAnimal = drawer.drawnAnimalByRarity(Rarity.SUPER_RARE);
        assertTrue(List.of("Shark", "OceanicPuffer","Orca", "Seal").contains(superRareAnimal));
    }
    @Test
    void testMythicalRarity() {
        DrawningAnimalsByTheirRarity drawer = new DrawningAnimalsByTheirRarity();
        String mythicAnimal = drawer.drawnAnimalByRarity(Rarity.MYTHIC);
        assertTrue(List.of("Whale","Turtle", "Crab", "Shrimp").contains(mythicAnimal));
    }
}

