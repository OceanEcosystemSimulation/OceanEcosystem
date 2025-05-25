package extendedMechanics;

import java.util.*;

// funkcja, która losuje zwierzęta, które należą do tej samej klasy rzadkości
public class DrawningAnimalsByTheirRarity implements IAnimalDrawer {
    private static final Map<Rarity, List<String>> rarityDrawning = new HashMap<>(); //przechowywanie przypisanie rarity do danych zwierząt

    static {
        rarityDrawning.put(Rarity.LEGENDARY, List.of("TralaleroTralala"));
        rarityDrawning.put(Rarity.MYTHIC, List.of("Starfish", "Crab")); //dla testu usuwam Orca i Whale z MYTHIC i daje do SUPER_RARE
        rarityDrawning.put(Rarity.SUPER_RARE, List.of( "OceanicPuffer","Orca","Whale","Shark", "Seal"));
        rarityDrawning.put(Rarity.RARE, List.of("Nemo","Octopus", "Dolphin"));
    }
    //TODO: Aktualizować mechaniki by dodawał się Tralalero tralala w trakcie (1% rarity itd)

    @Override
    public String drawnAnimalByRarity(Rarity rarity) {
        List<String> animalsList = rarityDrawning.get(rarity);
        int index = (int)(Math.random() * animalsList.size()); //losowanie indeksu    // - jest taki, że nie będzie można w przyszłości powtórzyć losowania,
        return animalsList.get(index); //zwracanie zwierzęcia                        // bo za każdym razem powinno być coś innego (ew. do zmiany)

    }
}
