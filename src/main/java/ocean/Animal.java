package ocean;

import java.util.*;

public abstract class Animal {
    private Coord position; //aktualne współrzędne w świecie
    private int foodLevel, age, loneliness;
    private final int  maxAge, maxLoneliness; //niezmienne po ustawieniu
    private int energy, health;
    private final Genes genes; //przechowuje geny - także raczej się nie zmienia po ustawieniu
    private final Gender gender; //niezmienne po ustawieniu
    private boolean alive = true;
    private String name;
    private final int id;
    private static int nextId = 1;

    protected static final Random rand = new Random();

    // konstruktor dla zwierząt startowych
    public Animal(Coord position, Genes genes, int maxAge, int maxLoneliness) {
        this.position = position;
        this.gender = rand.nextBoolean() ? Gender.MALE : Gender.FEMALE;
        this.age = 0;
        this.foodLevel = 100;
        this.energy = 100;
        this.health = 100;
        this.maxAge = maxAge; //losowanie w zwierzętach poszczególnych
        this.maxLoneliness = maxLoneliness; //losowanie w zwierzętach poszczególnych
        this.genes = genes; //losowanie w zwierzetach poszczegolnych
        this.id = nextId++;
    }

    // konstruktor dla dzieci
    public Animal(Coord position, Animal parent1, Animal parent2) {
        this.position = position;
        this.id = nextId++;
        this.genes = Genes.inherit(parent1.genes, parent2.genes);
        this.gender = rand.nextBoolean() ? Gender.MALE : Gender.FEMALE;
        this.age = 0;
        this.foodLevel = 100;
        this.energy = 100;
        this.health = 100;
        this.maxAge = (parent1.maxAge + parent2.maxAge) / 2; // dziedziczenie średnich wartości od rodziców
        this.maxLoneliness = (parent1.maxLoneliness + parent2.maxLoneliness) / 2; // dziedziczenie średnich wartości od rodziców
    }


    protected abstract void update(World world);

    public void die() {
        alive = false;
        System.out.println(this.getName() + " id: " + this.getId() + " is dead ");
    }


    //zostawiam to tutaj bo za wykładzie było że fajnie robić chyba takie pomiędzy a nie 1 do 1 połączenia
    protected void processLifeCycle(World world) {
        AnimalLifeManager.lifeCycle(world, this);
    }


    //czy moze sie rozmnażac - staty do zmiany - nie wiem gdzie dać tą metodę
    public boolean canReproduce() {
        return this.getEnergy()>=60 && this.getAge()>5 && this.getHealth()>=70; // przykładowe warunki
    }


    //idk czy nie za dużo tych getterów i setterów ;-;

    public Coord getPosition() {return position;}
    public int getAge() {return age;}
    public int getMaxAge() {return maxAge;}
    public int getFoodLevel() {return foodLevel;}
    public int getLoneliness() {return loneliness;}
    public int getMaxLoneliness() {return maxLoneliness;}
    public Genes getGenes() {return genes;}
    public Gender getGender() {return gender;}
    public boolean isAlive() {return alive;}
    public int getEnergy() {return energy;}
    public int getHealth() {return health;}
    public String getName(){return name;}
    public int getId() {return id;}

    public void setPosition(Coord newPosition) {this.position = newPosition;}
    public void setFoodLevel(int foodLevel) {this.foodLevel = foodLevel;}
    public void setAge(int age) {this.age = age;}
    public void setLoneliness(int loneliness) {this.loneliness = loneliness;}
    public void setName(String name) {this.name = name;}
    public void setEnergy(int energy) {this.energy = energy;}
    public void setHealth(int health) {this.health = health;}

}

