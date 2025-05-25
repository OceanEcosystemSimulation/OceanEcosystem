package ocean;

import allAnimals.Egg;
import body.*;
import map.Coord;
import map.Tile;

import java.util.*;

import static ocean.WorldSetup.*;

public class World {
    private final int width, height; //powirzchnia mapy
    private Tile[][] grid; //siatka - różne typy mapy i objekty
    private List<Animal> animals = new ArrayList<>(); //lista zwierząt na świecie
    private final List<Coord> coralReefCenter = new ArrayList<>(); // przechowuje współrzędne środka rafy koralowej
    private int currentTick = 0;


    private int eatenFoodCounter = 0; //do liczenia ile jedzenia zostało zjedzone od ostatnego uzupełnienia
    public int totalEatenFood = 0;
    public int deadAnimalCounter = 0;
    private boolean simulationEnded = false;

    //rozmieszczenie pól i zwierząt (na razie zawiera liczbę turn konkretną)
    public World(int width, int height, int noFood, int noCoral, int noAnimals) {
        this.width = width;
        this.height = height;
        this.grid = new Tile[width][height];
        initTiles(this, noCoral); //określa jakie jest dane pole mapy
        spawnAnimals(this, noAnimals); //spawn zwierząt randomowych według rarity
        spawnFood(this, noFood); //spawn jedzenie randomowo
    }


    //główna symulacja świata - w każdym cyklu aktualizuje zwierzęta - dead alive
    public void runSimulation(int tick) {
        List<Animal> currentAnimals = new ArrayList<>(animals); //tworzenie kopii by nie aktualizować m.in. dopiero co urodzonych
        this.currentTick = tick; //zapamietuje ture

        if (tick % 5 == 0 ) {  //co X tą turę dodaje brakujące jedzenie
            WorldSetup.spawnFood(this, eatenFoodCounter); //dodawanie jedzenia na mapę
            eatenFoodCounter = 0; //zerowanie licznika
        }

        for (Animal animal : currentAnimals) {  //iteracja po kopii animals - żeby nie było problemów później bo niektóre rzeczy usuwamy itp to sie rozwali inaczej
            animal.update(this); // aktualizuje stan zwierzęcia

            if (!animal.isAlive()) {
                System.out.println(animal.getName() + " id: " + animal.getId() + " is dead ");
                deadAnimalCounter++;
                animals.remove(animal); //usuwa martwe
            }
        }
    }

    public void endSimulation() {
        simulationEnded = true;
    }


    //zwraca listę zwierząt które znajdują się w pobliżu określonych współrzędnych Coord
    public List<Animal> getNearbyAnimals(Coord coord, int radius) {
        List<Animal> result = new ArrayList<>(); //lista do przechowywania zwierzat w okolicy
        for (Animal animal : animals) {
            if (animal.getPosition().distance(coord) <= radius && animal.isAlive()) //odległośc między zwierżeciem a c <= promień i czy żyje
                result.add(animal); //jeśli tak to dodaje do listy
        }
        return result;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getEatenFoodCounter() { return eatenFoodCounter; }
    public Tile[][] getGrid() { return grid; }
    public List<Animal> getAnimals() { return animals; }
    public boolean isSimulationEnded() { return simulationEnded; }
    public int getTicks() {
        return currentTick;
    }//zapamietuje ture


    //zwraca komórkę jeśli jest w granicach
    public Tile getTile(Coord coord) {
        return inBounds(coord.x, coord.y) ? grid[coord.x][coord.y] : null;
    }

    //sprawdza czy dane pole jest w zasięgu mapy
    public boolean inBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public boolean coordsInBounds(Coord coord) { // to samo co inBounds, po prostu zwraca oba pola
        return inBounds(coord.getX(), coord.getY());
    }

    //sprawdza czy dane pole jest zajete, zwraca true jeśli not empty
    public boolean isOccupied(Coord coord) { return !getNearbyAnimals(coord, 0).isEmpty(); }


    //dodaje zwierzeta do listy - potrzebuje ale może póżniej się da jakoś inaczej to załatwić
    public void addAnimal(Animal animal) {
        if (animal != null && animal.isAlive()) {
            animals.add(animal); //dodaje jeśli żywe
        }
    }

    public void addEgg(Egg egg) {
        addAnimal(egg);
    }

    public void setEatenFoodCounter(int eatenFoodCounter) {this.eatenFoodCounter = eatenFoodCounter;}

    public List<Coord> getCoralReefCenter() { return coralReefCenter; }
    public void addCoralReefCenter(Coord coord) { coralReefCenter.add(coord); }
}