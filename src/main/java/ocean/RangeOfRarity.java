package ocean;

import java.util.Random;

public class RangeOfRarity {

    public Rarity animalsDrawingByRarity () {
        int max = 100;
        int min = 1;
        int range = max - min + 1; // od 1 do 100
        int randomNumber = (int) (Math.random() * range) + min;
        if (randomNumber == 1) {
            return Rarity.LEGENDARY;
        } else if (randomNumber < 5) {
            return Rarity.MYTHIC;
        } else if (randomNumber < 16) {
            return Rarity.SUPER_RARE;
        } else {
            return Rarity.RARE;
        }
    }
}


