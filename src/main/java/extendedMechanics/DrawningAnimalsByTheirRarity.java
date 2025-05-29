package extendedMechanics;

import java.util.*;

// funkcja, która losuje zwierzęta, które należą do tej samej klasy rzadkości
public class DrawningAnimalsByTheirRarity implements IAnimalDrawer {
    private static final Map<Rarity, List<String>> rarityDrawning = new HashMap<>(); //przechowywanie przypisanie rarity do danych zwierząt

    static {
        //rarityDrawning.put(Rarity.LEGENDARY, new ArrayList<>());
        rarityDrawning.put(Rarity.MYTHIC, List.of("Whale","Turtle", "Crab", "Shrimp")); //dla testu usuwam Orca i Whale z MYTHIC i daje do SUPER_RARE
        rarityDrawning.put(Rarity.SUPER_RARE, List.of("Shark", "OceanicPuffer","Orca", "Seal"));
        rarityDrawning.put(Rarity.RARE, List.of("Octopus","Nemo", "Dolphin",  "Star"));
    }

    @Override
    public String drawnAnimalByRarity(Rarity rarity) {
        List<String> animalsList = rarityDrawning.get(rarity);
        int index = (int)(Math.random() * animalsList.size()); //losowanie indeksu    // - jest taki, że nie będzie można w przyszłości powtórzyć losowania,
        return animalsList.get(index); //zwracanie zwierzęcia                        // bo za każdym razem powinno być coś innego (ew. do zmiany)

    }
}
