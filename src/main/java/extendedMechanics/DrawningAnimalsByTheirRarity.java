package extendedMechanics;

import java.util.*;

// funkcja, która losuje zwierzęta, które należą do tej samej klasy rzadkości
public class DrawningAnimalsByTheirRarity implements IAnimalDrawer {
    private static final Map<Rarity, List<String>> rarityDrawning = new HashMap<>(); //przechowywanie przypisanie rarity do danych zwierząt

    static {
        rarityDrawning.put(Rarity.LEGENDARY, List.of("TralaleroTralala"));
        rarityDrawning.put(Rarity.MYTHIC, List.of("Starfish", "Whale", "Seal", "Crab", "Octopus")); //dla testu usuwam Orca z MYTHIC i daje do SUPER_RARE
        rarityDrawning.put(Rarity.SUPER_RARE, List.of("Shark", "Dolphin", "Orca"));
        rarityDrawning.put(Rarity.RARE, List.of("Nemo","OceanicPuffer", "Noname fish cuz it does not exists yet"));
    }
    //TODO: Aktualizować mechaniki by dodawał się Tralalero tralala w trakcie

    @Override
    public String drawnAnimalByRarity(Rarity rarity) {
        List<String> animalsList = rarityDrawning.get(rarity);
        int index = (int)(Math.random() * animalsList.size()); //losowanie indeksu    // - jest taki, że nie będzie można w przyszłości powtórzyć losowania,
        return animalsList.get(index); //zwracanie zwierzęcia                        // bo za każdym razem powinno być coś innego (ew. do zmiany)

    }
}
