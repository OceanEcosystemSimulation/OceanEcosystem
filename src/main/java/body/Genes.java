package body;

import java.util.Random;

/* -------------------------------CONSTANTS------------------------------- */

//geny i ich mechanika (ogólnie wydaje mi się że latwo tak dodawać inne nowe jak trzeba)
public class Genes {

    private static final Random rand = new Random();

    private int strength;
    private int speed; //wartości max 5 kratek - zależy od tego ile się przesuwa/widzi
    private Gender gender;

    private int fertility;
    private int maxAge;
    private int maxLoneliness;

    /* -------------------------------INHERIT------------------------------- */

    //geny potomstwa (trzeba przenieść maxAge i maxLoneliness też raczej)
    public static Genes inherit(Genes g1, Genes g2) {
        Genes g = new Genes();

        g.strength = mutate((g1.strength + g2.strength) / 2);
        g.speed = mutate((g1.speed + g2.speed) / 2);

        g.gender = rand.nextBoolean() ? Gender.FEMALE : Gender.MALE;

        g.fertility = mutate((g1.fertility + g2.fertility) / 2);
        g.maxAge = mutate((g1.maxAge + g2.maxAge) / 2);
        g.maxLoneliness = mutate((g1.maxLoneliness + g2.maxLoneliness) / 2);

        return g;
    }

    // LOSOWE, DLA PIERWSZEGO ORGANIZMU, KTÓRY POJAWI SIĘ NA MAPIE
    public static Genes randomGenes() {
        Genes g = new Genes();
        g.strength      = 5  + rand.nextInt(10);
        g.speed         = 8  + rand.nextInt(10);
        g.gender        = rand.nextBoolean() ? Gender.FEMALE : Gender.MALE;
        g.fertility     = 20 + rand.nextInt(10);
        g.maxAge        = 72 + rand.nextInt(60);
        g.maxLoneliness = 35 + rand.nextInt(20);
        return g;
    }


    //losowa mutacja +-2 do genów
    private static int mutate(int value) {return value + (int)(Math.random() * 5 - 2);}

    /* -------------------------------GETTERY------------------------------- */

    public int getStrength() {return strength;}
    public int getSpeed() {return speed;}
    public Gender getGender() {return gender;}
    public int getFertility() {return fertility;}
    public int getMaxAge() {return maxAge;}
    public int getMaxLoneliness() {return maxLoneliness;}

    /* -------------------------------SETTERY------------------------------- */

    public void setStrength(int strength) {this.strength = strength;}
    public void setSpeed(int speed) {this.speed = speed;}
    public void setGender(Gender g) {this.gender = g;}
    public void setFertility(int f) {fertility = f;}
    public void setMaxAge(int a) {maxAge = a;}
    public void setMaxLoneliness(int l) {maxLoneliness = l;}
}

