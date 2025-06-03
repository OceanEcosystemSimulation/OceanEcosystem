package body;

import ocean.World;

//geny i ich mechanika
/**
 * Genes class represents the genetic traits of an organism.
 */
public class Genes {
    //encapsulation
    private int strength;
    private int speed; //wartości X kratek - zależy od tego ile się przesuwa/widzi
    private int maxAge;
    private int maxLoneliness;
    private int maxEnergy;


    /**
     * Creates a new set of genes for child by averaging the values of two parent gene sets and applying random mutation to each trait.
     * @param g1 The first parent's genes.
     * @param g2 The second parent's genes.
     * @return A new Genes object representing the inherited genes.
     */
    public static Genes inherit(Genes g1, Genes g2) {
        Genes genes = new Genes();
        genes.strength = mutate((g1.strength + g2.strength) / 2);
        genes.speed = mutate((g1.speed + g2.speed) / 2);
        genes.maxAge = mutate((g1.maxAge + g2.maxAge) / 2);
        genes.maxLoneliness = mutate((g1.maxLoneliness + g2.maxLoneliness) / 2);
        genes.maxEnergy = mutate((g1.maxEnergy + g2.maxEnergy) / 2);
        return genes;
    }


    //losowa mutacja do genów
    /**
     * Applies a random mutation to a given gene value with a 5% chance.
     * The mutation changes the value by ±2, ensuring it isn't below 1.
     * @param value The original gene value.
     * @return The mutated or unchanged gene value.
     */
    public static int mutate(int value) {
        if (World.random.nextDouble() < 0.05) { //nie zawsze (losowo) zachodzi - 5%
            return Math.max(1, value + World.random.nextInt(5) - 2); //mutuje w zakresie +-2
        }
        return Math.max(1, value);
    }


    /**
     * Sets the speed gene value, optionally adjusting it based on the world's minimum map size.
     * If the map is very small, the speed is scaled.
     * @param baseSpeed The base speed value to set.
     */
    public void setSpeed(int baseSpeed) {
        if (World.minMapSize < 2) {
            this.speed = Math.max(1, mutate((int)(baseSpeed * (double) World.minMapSize / 20)));
        } else {
            this.speed = baseSpeed;
        }
    }


    /* -------------------------------GETTERY------------------------------- */

    public int getStrength() {return strength;}
    public int getSpeed() {return speed;}
    public int getMaxAge() {return maxAge;}
    public int getMaxLoneliness() {return maxLoneliness;}
    public int getMaxEnergy() {return maxEnergy;}

    /* -------------------------------SETTERY------------------------------- */

    public void setStrength(int strength) {this.strength = strength;}
    public void setMaxAge(int maxAge) {this.maxAge = maxAge;}
    public void setMaxLoneliness(int maxLoneliness) {this.maxLoneliness = maxLoneliness;}
    public void setMaxEnergy(int maxEnergy) {this.maxEnergy = maxEnergy;}
}

