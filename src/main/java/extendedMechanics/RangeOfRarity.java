package extendedMechanics;

import ocean.World;

/**
 * Determines the rarity classification for animals based on a random draw.
 */
public class RangeOfRarity {
    /**
     * Assigns a rarity level based on a randomly generated number between 1 and 100.
     * @return The assigned Rarity classification.
     */
    public Rarity animalsDrawingByRarity () {
        int max = 100;
        int min = 1;
        int randomNumber = World.random.nextInt(max) + min; //od 1 do 100 włącznie
        if (randomNumber < 6) { //1-5: 5%
            return Rarity.MYTHIC;
        } else if (randomNumber < 17) { //6-16: 11%
            return Rarity.SUPER_RARE;
        } else {  //17-100: 84%
            return Rarity.RARE;
        }
    }
}


