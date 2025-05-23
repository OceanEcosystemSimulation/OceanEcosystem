package body;

import map.*;
import ocean.World;

import java.util.*;

public abstract class Animal {

    /* -------------------------------STAŁE------------------------------- */

    private Coord position; //aktualne współrzędne w świecie
    private int foodLevel, age, loneliness;
    private int energy, health;
    private final Genes genes; //przechowuje geny - także raczej się nie zmienia po ustawieniu
    private final Gender gender; //przechowuje płeć - niezmienne po ustawieniu
    private boolean alive = true;
    private String name;
    private final int id;
    private static int nextId = 1;
    private boolean pregnant = false;
    private int pregnancyCounter = 0;
    private Animal fatherDuringPregnancy = null;

    public static final Random rand = new Random();

    /* -------------------------------KONSTRUKTORY------------------------------- */

    // konstruktor dla zwierząt startowych
    public Animal(Coord position, Genes genes) {
        this.position = position;
        this.age = 0;
        this.foodLevel = 100;
        this.energy = 100;
        this.health = 100;
        this.gender = rand.nextBoolean() ? Gender.MALE : Gender.FEMALE;
        this.genes = genes; //losowanie w zwierzetach poszczegolnych
        this.id = nextId++;
    }

    // konstruktor dla dzieci
    public Animal(Coord position, Animal parent1, Animal parent2) {
        this(position, Genes.inherit(parent1.genes, parent2.genes));
    }


    public abstract void update(World world);

    public abstract Animal giveBirth(Coord pos, Animal parent1, Animal parent2);

    public void die() {alive = false;}

    //zostawiam to tutaj bo za wykładzie było że fajnie robić chyba takie pomiędzy a nie 1 do 1 połączenia
    protected void processLifeCycle(World world) {
        AnimalLifeManager.lifeCycle(world, this);
    }

    /* -------------------------------ZATRUCIE OD OCEANIC PUFFER------------------------------- */

    private int poisonTicks = 4; // ile tur efekt zatrucia ma się utrzymywać na rybce

    private void poison() {
        if (poisonTicks > 0) {
            setHealth(getHealth() - 5); // odejmuje 5HP co turę
            poisonTicks--; // dekrementuje licznik co turę
        }
    }


    /* -------------------------------GETTERY------------------------------- */

    public Coord getPosition() {return position;}
    public int getAge() {return age;}
    public int getFoodLevel() {return foodLevel;}
    public int getLoneliness() {return loneliness;}
    public Genes getGenes() {return genes;}
    public Gender getGender() {return gender;}
    public boolean isAlive() {return alive;}
    public int getEnergy() {return energy;}
    public int getHealth() {return health;}
    public String getName(){return name;}
    public int getId() {return id;}
    public boolean isPregnant() {return pregnant;}
    public int getPregnancyCounter() {return pregnancyCounter;}
    public Animal getFatherDuringPregnancy() {return fatherDuringPregnancy;}

    /* -------------------------------SETTERY------------------------------- */

    public void setPosition(Coord newPosition) {
        this.position = newPosition;
        System.out.println(this.getName() + " id: " + this.getId() + "  jumped to [" + this.getPosition().x + "," + this.getPosition().y + "]");
    }

    public void setFoodLevel(int foodLevel) {this.foodLevel = foodLevel;}
    public void setAge(int age) {this.age = age;}
    public void setLoneliness(int loneliness) {this.loneliness = loneliness;}
    public void setName(String name) {this.name = name;}
    public void setEnergy(int energy) {this.energy = energy;}
    public void setHealth(int health) {this.health = health;}
    public void setPregnant(boolean pregnant) {this.pregnant = pregnant;}
    public void setPregnancyCounter(int counter) {this.pregnancyCounter = counter;}
    public void setFatherDuringPregnancy(Animal father) {fatherDuringPregnancy = father;}
}

