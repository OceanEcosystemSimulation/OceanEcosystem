package extendedMechanics;

import ocean.World;

import java.util.*;

// funkcja, która losuje zwierzęta, które należą do tej samej klasy rzadkości
public class DrawningAnimalsByTheirRarity implements IAnimalDrawer {
    private static final Map<Rarity, List<String>> rarityDrawning = new HashMap<>(); //przechowywanie przypisanie rarity do danych zwierząt

    static {
        //rarityDrawning.put(Rarity.LEGENDARY, new ArrayList<>());
        rarityDrawning.put(Rarity.MYTHIC, List.of("Whale","Turtle", "Crab", "Shrimp"));
        rarityDrawning.put(Rarity.SUPER_RARE, List.of("Shark", "OceanicPuffer","Orca", "Seal"));
        rarityDrawning.put(Rarity.RARE, List.of("Nemo", "Dolphin",  "Star", "Octopus"));
    }

    @Override
    public String drawnAnimalByRarity(Rarity rarity) {
        List<String> animalsList = rarityDrawning.get(rarity);
        int index = World.random.nextInt(animalsList.size()); //losowanie indeksu
        return animalsList.get(index); //zwracanie zwierzęcia
    }
}
