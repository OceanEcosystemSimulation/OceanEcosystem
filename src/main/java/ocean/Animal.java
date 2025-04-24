package ocean;

import java.util.*;

public abstract class Animal {
    protected Coord position; //aktualne współrzędne w świecie
    protected int foodLevel, age, maxAge, loneliness, maxLoneliness;
    protected boolean alive = true;
    protected Genes genes; //przechowuje geny
    protected Gender gender;
    protected String name;
    protected Rarity rarity;

    protected Random rand = new Random();

    // konstruktor dla zwierząt startowych
    public Animal(Coord position) {
        this.position = position;
        this.genes = Genes.randomGenes();
        this.gender = rand.nextBoolean() ? Gender.MALE : Gender.FEMALE;
        this.age = 0;
        this.foodLevel = 100;
        this.maxAge = 0; //losowanie w zwierzętach poszczególnych
        this.maxLoneliness = 0; //losowanie w zwierzętach poszczególnych
    }

    // konstruktor dla dzieci
    public Animal(Coord position, Animal parent1, Animal parent2) {
        this.position = position;
        this.genes = Genes.inherit(parent1.genes, parent2.genes);
        this.gender = rand.nextBoolean() ? Gender.MALE : Gender.FEMALE;
        this.age = 0;
        this.foodLevel = 100;
        this.maxAge = (parent1.maxAge + parent2.maxAge) / 2; // dziedziczenie średnich wartości od rodziców
        this.maxLoneliness = (parent1.maxLoneliness + parent2.maxLoneliness) / 2; // dziedziczenie średnich wartości od rodziców
    }

    public void processLifeCycle() {
        age++;
        foodLevel--;
        loneliness++;

        if (foodLevel <= 0 || age > maxAge || loneliness > maxLoneliness) {
            alive = false;
        }
    }



    public abstract void update(World world);

    public Coord getPosition() {
        return position;
    }
}

