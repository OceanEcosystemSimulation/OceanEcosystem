package extendedMechanics;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.List;

class DrawningAnimalsByTheirRarityTest {

    @Test
    void testRareRarity() {
        DrawningAnimalsByTheirRarity drawer = new DrawningAnimalsByTheirRarity(); //tworzenie instancji
        String rareAnimal = drawer.drawnAnimalByRarity(Rarity.RARE); //losowanie zwierzęcia z RARE
        assertTrue(List.of("Nemo", "Noname fish cuz it does not exists yet").contains(rareAnimal)); //czy zawiera
    }

    @Test
    void testSuperRaraRarity() {
        DrawningAnimalsByTheirRarity drawer = new DrawningAnimalsByTheirRarity();
        String superRareAnimal = drawer.drawnAnimalByRarity(Rarity.SUPER_RARE);
        assertTrue(List.of("Shark", "Dolphin", "Oceanic_puffer").contains(superRareAnimal));
    }
    @Test
    void testMythicalRarity() {
        DrawningAnimalsByTheirRarity drawer = new DrawningAnimalsByTheirRarity();
        String mythicAnimal = drawer.drawnAnimalByRarity(Rarity.MYTHIC);
        assertTrue(List.of("Starfish", "Whale", "Orca", "Seal", "Crab", "Octopus").contains(mythicAnimal));
    }


    @Test
    void testLegendaryRarity() {
        DrawningAnimalsByTheirRarity drawer = new DrawningAnimalsByTheirRarity();
        assertEquals("TralaleroTralala", drawer.drawnAnimalByRarity(Rarity.LEGENDARY));
    }
}

