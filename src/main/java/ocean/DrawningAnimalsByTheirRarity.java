package ocean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// funkcja, która losuje zwierzęta, które należą do tej samej klasy rzadkości
public class DrawningAnimalsByTheirRarity implements IAnimalDrawer {
    private static final Map<Rarity, List<String>> rarityDrawning = new HashMap<>(); //przechowywanie przypisanie rarity do danych zwierząt

    static {
        rarityDrawning.put(Rarity.LEGENDARY, List.of("TralaleroTralala"));
        rarityDrawning.put(Rarity.MYTHIC, List.of("Starfish", "Whale", "Grind", "Seal", "Crab", "Octopus"));
        rarityDrawning.put(Rarity.SUPER_RARE, List.of("Shark", "Dolphin", "Oceanic_puffer"));
        rarityDrawning.put(Rarity.RARE, List.of("Nemo", "Noname fish cuz it does not exists yet"));
    }

    @Override
    public String drawnAnimalByRarity(Rarity rarity) {
        List<String> animalsList = rarityDrawning.get(rarity);
        int index = (int)(Math.random() * animalsList.size()); //losowanie indeksu    // - jest taki, że nie będzie można w przyszłości powtórzyć losowania,
        return animalsList.get(index); //zwracanie zwierzęcia                        // bo za każdym razem powinno być coś innego (ew. do zmiany)

    }
}
