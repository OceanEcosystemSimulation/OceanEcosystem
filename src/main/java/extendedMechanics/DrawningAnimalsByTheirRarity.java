package extendedMechanics;

import ocean.World;

import java.util.*;

/**
 * Handles the random selection of animals based on their rarity class.
 * Implements IAnimalDrawer.
 */
public class DrawningAnimalsByTheirRarity implements IAnimalDrawer {
    private static final Map<Rarity, List<String>> rarityDrawning = new HashMap<>(); //przechowywanie przypisanie rarity do danych zwierząt

    /**
     * Initializes the mapping of rarity levels to animal types.
     */
    static {
        rarityDrawning.put(Rarity.MYTHIC, List.of("Whale","Turtle", "Crab", "Shrimp"));
        rarityDrawning.put(Rarity.SUPER_RARE, List.of("Shark", "OceanicPuffer","Orca", "Seal"));
        rarityDrawning.put(Rarity.RARE, List.of("Nemo", "Dolphin",  "Star", "Octopus"));
    }

    /**
     * Randomly selects an animal from the specified rarity group.
     * @param rarity The rarity class used for drawing an animal.
     * @return The name of the randomly selected animal.
     */
    @Override
    public String drawnAnimalByRarity(Rarity rarity) {  //implementation
        List<String> animalsList = rarityDrawning.get(rarity);
        int index = World.random.nextInt(animalsList.size()); //losowanie indeksu
        return animalsList.get(index); //zwracanie zwierzęcia
    }
}
