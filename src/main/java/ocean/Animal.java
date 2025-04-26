package ocean;

import java.util.*;

public abstract class Animal {
    private Coord position; //aktualne współrzędne w świecie
    private int foodLevel, age, maxAge, loneliness, maxLoneliness;
    private int energy, health;
    private Genes genes; //przechowuje geny
    private Gender gender;
    private boolean alive = true;
    private String name;

    protected Random rand = new Random();

    // konstruktor dla zwierząt startowych
    public Animal(Coord position) {
        this.position = position;
        this.gender = rand.nextBoolean() ? Gender.MALE : Gender.FEMALE;
        this.age = 0;
        this.foodLevel = 100;
        this.energy = 100;
        this.health = 100;
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
        this.energy = 100;
        this.health = 100;
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

    public void die() {alive = false;}



    //idk gdzie to dać szczerze - ew się przeniesie do Genes
    public double getEffectiveStrength() {
        return getGenes().getStrength() * (getEnergy()/100.0);
    }

    public double getEffectiveSpeed() {
        return getGenes().getSpeed() * (getEnergy()/100.0);
    }

    public double getCombatPower() {
        return getEffectiveStrength() * 0.7 + getEffectiveSpeed() * 0.3;
    }

    //ucieczka z walki - nie podoba mi się że jest argument world ale idk jak to zrobić
    public void escape(Animal attacker, World world) {
        Coord pos = getPosition();
        int distance = (int) (1.2*getGenes().getSpeed()); //ma większą prędkość w walce minimalnie (adrenalina XD) - do zmiany możliwej

        //losowy kierunek ucieczki na pełną odległość dlatego nie randomMove z wyżej
        int dx = rand.nextBoolean() ? distance : -distance;
        int dy = rand.nextBoolean() ? distance : -distance;
        Coord escapePos = new Coord(pos.x + dx, pos.y + dy);
        setPosition(world.inBounds(escapePos.x, escapePos.y) ? escapePos //czy poza granice
                : pos.randomAdjacent(world.getWidth(), world.getHeight(), getGenes().getSpeed()));
    }

    public void takeDamage(double amount) {
        int newHealth = (int)(health - amount);
        health = Math.max(newHealth, 0);
        if (health <= 0) {die();} //nie żyje
    }



    public Coord getPosition() {return position;}
    public int getAge() {return age;}
    public int getFoodLevel() {return foodLevel;}
    public int getLoneliness() {return loneliness;}
    public Genes getGenes() {return genes;}
    public Gender getGender() {return gender;}
    public boolean isAlive() {return alive;}
    public int getEnergy() {return energy;}
    public int getHealth() {return health;}

    public void setPosition(Coord newPosition) {this.position = newPosition;}
    public void setFoodLevel(int foodLevel) {this.foodLevel = foodLevel;} //setter a nie add bo może będziemy chcieli coś więcej niż add
    public void setMaxAge(int maxAge) {this.maxAge = maxAge;}
    public void setMaxLoneliness(int maxLoneliness) {this.maxLoneliness = maxLoneliness;}
    public void setGenes(Genes genes) {this.genes = genes;}

}

