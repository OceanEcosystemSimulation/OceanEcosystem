package extendedMechanics;

import ocean.World;

public class RangeOfRarity {

    public Rarity animalsDrawingByRarity () {
        int max = 100;
        int min = 1;
        int randomNumber = World.random.nextInt(max) + min; //od 1 do 100 włącznie
        if (randomNumber < 5) {
            return Rarity.MYTHIC;
        } else if (randomNumber < 16) {
            return Rarity.SUPER_RARE;
        } else {
            return Rarity.RARE;
        }
    }
}


