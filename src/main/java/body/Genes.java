package body;

import ocean.World;

//geny i ich mechanika
public class Genes {
    private int strength;
    private int speed; //wartości X kratek - zależy od tego ile się przesuwa/widzi
    private int maxAge;
    private int maxLoneliness;
    private int maxEnergy;


    /* -------------------------------INHERIT------------------------------- */

    //geny potomstwa
    public static Genes inherit(Genes g1, Genes g2) {
        Genes genes = new Genes();
        genes.strength = mutate((g1.strength + g2.strength) / 2);
        genes.speed = mutate((g1.speed + g2.speed) / 2);
        genes.maxAge = mutate((g1.maxAge + g2.maxAge) / 2);
        genes.maxLoneliness = mutate((g1.maxLoneliness + g2.maxLoneliness) / 2);
        genes.maxEnergy = mutate((g1.maxEnergy + g2.maxEnergy) / 2);
        return genes;
    }

    public void setSpeed(int baseSpeed) {
        if (World.minMapSize < 2) {
            this.speed = Math.max(1, mutate((int)(baseSpeed * (double) World.minMapSize / 20)));
        } else {
            this.speed = baseSpeed;
        }
    }

    //losowa mutacja do genów
    public static int mutate(int value) {
        if (World.random.nextDouble() < 0.1) { //nie zawsze (losowo) zachodzi - X%
            return Math.max(1, value + World.random.nextInt(5) - 2); //mutuje w zakresie +-2
        }
        return Math.max(1, value);
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

