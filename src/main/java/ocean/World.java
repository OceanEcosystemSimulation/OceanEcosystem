package ocean;

import allAnimals.Egg;
import body.*;
import extendedMechanics.DrawningAnimalsByTheirRarity;
import map.Coord;
import map.Tile;

import java.util.*;

import static ocean.WorldSetup.*;

public class World {
    private final int width, height; //powirzchnia mapy
    private Tile[][] grid; //siatka - różne typy mapy i objekty
    private final List<Animal> animals = new ArrayList<>(); //lista zwierząt na świecie
    private final List<WorldObject> objects = new ArrayList<>(); //lista objektów na świecie aka wszystko co jest
    private final List<Coord> coralReefCenter = new ArrayList<>(); // przechowuje współrzędne środka rafy koralowej
    public static int minMapSize;

    private int eatenFoodCounter = 0; //do liczenia ile jedzenia zostało zjedzone od ostatnego uzupełnienia
    public int totalEatenFood = 0;
    public int deadAnimalCounter = 0;
    private boolean simulationEnded = false;

    public static Random random;


    //rozmieszczenie pól i zwierząt (na razie zawiera liczbę turn konkretną)
    public World(int width, int height, int noFood, int noCoral, int noAnimals) {
        this.width = width;
        this.height = height;
        this.grid = new Tile[width][height];
        initTiles(this, noCoral); //określa jakie jest dane pole mapy
        spawnAnimals(this, noAnimals); //spawn zwierząt randomowych według rarity
        spawnFood(this, noFood); //spawn jedzenie randomowo
        minMapSize = Math.min(width, height);
    }

    private boolean tralaleroSpawned = false;

    private void addTralaleroTralala() {
        if (!tralaleroSpawned && random.nextInt(100) == 0) { // 1% szans
            DrawningAnimalsByTheirRarity.addTralalero();
            tralaleroSpawned = true;
            SimulationStatsManager.writeToFile("Final bosss spawned, end is comming...");

            Coord coord; // dodaje do swiata jesli moze
            do {
                coord = WorldSetup.randomCoord(this);
            } while (isOccupied(coord));

                Animal tralaleroTralala = WorldSetup.createAnimalFromName("TralaleroTralala", coord);
            if (tralaleroTralala != null) {
                addObject(tralaleroTralala);
            }
        }
    }


    //główna symulacja świata - w każdym cyklu aktualizuje zwierzęta - dead alive
    public void runSimulation(int tick) {

        addTralaleroTralala(); // próbuje dodac tralalero ale rarity to 1%, więc co tick
        List<WorldObject> currentObjects = new ArrayList<>(objects); //tworzenie kopii by nie aktualizować m.in. dopiero co urodzonych
        Coord.allAtempts = 0;

        if (tick % 5 == 0 ) {  //co X tą turę dodaje brakujące jedzenie
            WorldSetup.spawnFood(this, eatenFoodCounter); //dodawanie jedzenia na mapę
            eatenFoodCounter = 0; //zerowanie licznika
        }

        for (WorldObject object : currentObjects) {  //iteracja po kopii animals - żeby nie było problemów później bo niektóre rzeczy usuwamy itp to sie rozwali inaczej
            object.update(this); // aktualizuje stan zwierzęcia

            if (object instanceof Animal animal && !animal.isAlive()) { //jeżeli to zwierze i nie żyje
                SimulationStatsManager.writeToFile(animal.getName() + " id: " + animal.getId() + " is dead ");
                deadAnimalCounter++;
                removeObject(animal);
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


    public void setEatenFoodCounter(int eatenFoodCounter) {this.eatenFoodCounter = eatenFoodCounter;}

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getEatenFoodCounter() { return eatenFoodCounter; }
    public Tile[][] getGrid() { return grid; }
    public List<Animal> getAnimals() { return animals; }
    public List<WorldObject> getObjects() { return objects; }
    public boolean isSimulationEnded() { return simulationEnded; }


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


    //dodaje objekty do listy
    public void addObject(WorldObject object) {
        if (object == null) { return; }

        objects.add(object); //wszystkie
        if (object instanceof Animal animal && animal.isAlive()) {  //tylko zwierzeta
            animals.add(animal);
        }
    }


    //usuwa objekty z lisy - potrzebuje ale może póżniej się da jakoś inaczej to załatwić
    public void removeObject(WorldObject object) {
        if (object instanceof Animal animal && !animal.isAlive()) { //tylko zwierzeta
            animals.remove(animal);
            objects.remove(object);
        } else { objects.remove(object); } //wszystko
    }


    public void addEgg(Egg egg) {
        addObject(egg);
    }


    public List<Coord> getCoralReefCenter() { return coralReefCenter; }
    public void addCoralReefCenter(Coord coord) { coralReefCenter.add(coord); }
}