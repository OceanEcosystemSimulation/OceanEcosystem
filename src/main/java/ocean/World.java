package ocean;

import allAnimals.Egg;
import body.*;
import map.Coord;
import map.Tile;

import java.util.*;

import static ocean.WorldSetup.*;

public class World {
    public static final int TILE_SIZE = 10;

    private final int width, height; //powirzchnia mapy
    private Tile[][] grid; //siatka - różne typy mapy i objekty
    private List<Animal> animals = new ArrayList<>(); //lista zwierząt na świecie

    //rozmieszczenie pól i zwierząt (na razie zawiera liczbę turn konkretną)
    public World(int width, int height, int noFood, int noCoral, int noAnimals, int ticks) {
        this.width = width;
        this.height = height;
        this.grid = new Tile[width][height];
        initTiles(this, noCoral); //określa jakie jest dane pole mapy
        spawnAnimals(this, noAnimals); //spawn zwierząt randomowych według rarity
        spawnFood(this, noFood); //spawn jedzenie randomowo
    }


    //główna symulacja świata - w każdym cyklu aktualizuje zwierzęta - dead alive
    public void runSimulation(int ticks) {
        for (int t = 0; t < ticks; t++) {
            List<Animal> currentAnimals = new ArrayList<>(animals); //tworzenie kopii by nie aktualizować m.in. dopiero co urodzonych

            for (Animal animal : currentAnimals) {  //iteracja po kopii animals - żeby nie było problemów później bo niektóre rzeczy usuwamy itp to sie rozwali inaczej
                animal.update(this); // aktualizuje stan zwierzęcia

                if (!animal.isAlive()) {
                    System.out.println(animal.getName() + " id: " + animal.getId() + " is dead ");
                    animals.remove(animal); //usuwa martwe
                }
            }
        }
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

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Tile[][] getGrid() {
        return grid;
    }

    public List<Animal> getAnimals() { return animals; }

    //zwraca komórkę jeśli jest w granicach
    public Tile getTile(Coord coord) {
        return inBounds(coord.x, coord.y) ? grid[coord.x][coord.y] : null;
    }

    //sprawdza czy dane pole jest w zasięgu mapy
    public boolean inBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
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
}