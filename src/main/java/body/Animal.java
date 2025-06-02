package body;

import allAnimals.Skeleton;
import map.*;
import ocean.SimulationStatsManager;
import ocean.World;
import soundEffects.SoundPlayer;


//abstract class inheriting from WorldObject - defines the general characteristics and behaviors of all animals;
public abstract class Animal extends WorldObject {
    //encapsulation – characteristic fields are private and accessible only through methods
    private int foodLevel, age, loneliness;
    private int energy, health;
    private final Genes genes;  //composition – Animal has Genes as its element (it is part of the internal structure)
    private final Gender gender;
    private boolean alive = true;
    private String name;
    private final int id;
    private static int nextId = 1;
    private boolean pregnant = false;
    private int pregnancyCounter = 0;
    private Animal fatherDuringPregnancy = null;
    private boolean lonelinessReseted = false; //do sprawdzania czy w danej turze wyzerowano loneliness poprzez inne zwierze


    /* -------------------------------KONSTRUKTORY------------------------------- */

    // konstruktor dla zwierząt startowych
    public Animal(Coord position, Genes genes) {
        super(position);
        this.age = 0;
        this.foodLevel = 100;
        this.energy = 100;
        this.health = 100;
        this.gender = World.random.nextBoolean() ? Gender.MALE : Gender.FEMALE;
        this.genes = genes; //losowanie w zwierzetach poszczegolnych
        this.id = nextId++;
    }

    // konstruktor dla dzieci
    public Animal(Coord position, Animal parent1, Animal parent2) {
        this(position, Genes.inherit(parent1.genes, parent2.genes));
    }


    //polymorphism (in animals) - this method is overridden in animals and its body is added specifically for each animal
    public abstract Animal giveBirth(Coord pos, Animal parent1, Animal parent2);


    public void die(World world) {
        alive = false;
        world.deadAnimalCounter++;
        world.addObject(new Skeleton(this.getPosition())); // dodajemy szkielet, dla konkretnego zwierzęcia, na konkretnej pozycji
        SoundPlayer.playSound("sounds/oof.wav");
    }

    protected void processLifeCycle(World world) {
        AnimalLifeManager.lifeCycle(world, this);
    }


    /* -------------------------------ZATRUCIE OD OCEANIC PUFFER------------------------------- */

    //encapsulation
    private int poisonTicks = 0; // ile tur efekt zatrucia ma się utrzymywać na rybce

    public int getPoisonTicks() {return poisonTicks;}
    public void setPoisonTicks(int ticks) {this.poisonTicks = ticks;}


    /* -------------------------------INK------------------------------- */

    //encapsulation
    private int slowCounter = 0;  //ile tur efekt spowolnienia ma się utrzymywać na rybce

    public int getSlowCounter() {return slowCounter;}
    public void setSlowCounter(int turns) {this.slowCounter = turns;}



    /* -------------------------------GETTERY------------------------------- */

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
    public boolean getLonelinessReseted() {return lonelinessReseted;}


    /* -------------------------------SETTERY------------------------------- */

    @Override
    public void setPosition(Coord newPosition) {
        super.setPosition(newPosition);
        SimulationStatsManager.writeToFile(this.getName() + "," + this.getId() + ",jumped to,,,[" + this.getPosition().getX() + ";" + this.getPosition().getY() + "]\n");
    }

    public void setFoodLevel(int foodLevel) {this.foodLevel = foodLevel;}
    public void setAge(int age) {this.age = age;}
    public void setLoneliness(int loneliness) {this.loneliness = loneliness;}
    public void setName(String name) {this.name = name;}
    public void setEnergy(int energy) {this.energy = energy;}
    public void setHealth(int health) {this.health = health;}
    public void setPregnant(boolean pregnant) {this.pregnant = pregnant;}
    public void setPregnancyCounter(int counter) {this.pregnancyCounter = counter;}
    public void setFatherDuringPregnancy(Animal father) {this.fatherDuringPregnancy = father;}
    public void setLonelinessReseted(boolean status) {this.lonelinessReseted = status;}
}

