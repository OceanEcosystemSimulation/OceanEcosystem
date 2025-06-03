package ocean;

import allAnimals.Egg;
import body.*;
import map.Coord;
import map.MapType;
import map.Tile;

import java.util.*;

import static ocean.WorldSetup.*;

/**
 * Represents simulations world containing animals, terrain, and food sources.
 * Handles simulation logic such as adding objects and running simulation cycles.
 */
public class World {
    //encapsulation (poza map size)
    private final int width, height; //powirzchnia mapy
    private Tile[][] grid; //siatka - różne typy mapy i objekty   //composition
    private final List<Animal> animals = new ArrayList<>(); //lista zwierząt na świecie
    private final List<WorldObject> objects = new ArrayList<>(); //lista objektów na świecie aka wszystko co jest   //aggregation
    private final List<Coord> coralReefCenter = new ArrayList<>(); // przechowuje współrzędne środka rafy koralowej   //aggregation
    public static int minMapSize; //trzyma jaki jest najmniejszy wymiar mapy

    private int eatenFoodCounter = 0; //do liczenia ile jedzenia zostało zjedzone od ostatnego uzupełnienia   //encapsulation
    public int totalEatenFood = 0; //całe zjedzone jedzenie w od początku symulacji
    public int deadAnimalCounter = 0; //ile zwierzat umarło od poczatku symulacji
    private boolean simulationEnded = false; //czy symulacja zakończona (do wymuszenia końca)   //encapsulation

    public static Random random;


    //konstruktor świata
    public World(int width, int height, int noFood, int noCoral, int noAnimals) {
        this.width = width;
        this.height = height;
        this.grid = new Tile[width][height];
        initTiles(this, noCoral); //określa jakie jest dane pole mapy
        spawnAnimals(this, noAnimals); //spawn zwierząt randomowych według rarity
        spawnFood(this, noFood); //spawn jedzenie randomowo
        minMapSize = Math.min(width, height); //jaki jest najmniejszy wymiar mapy
    }



    /**
     * Runs the simulation step, updating objects, processing food, and checking for alive animals.
     * @param tick The current simulation tick.
     */
    public void runSimulation(int tick) {
        addTralaleroTralala(); // próbuje dodac tralalero ale rarity to 0.5%, więc co tick
        List<WorldObject> currentObjects = new ArrayList<>(objects); //tworzenie kopii by nie aktualizować m.in. dopiero co urodzonych
        Coord.allAtempts = 0;

        if (tick % 5 == 0 ) {  //co X tą turę dodaje brakujące jedzenie
            WorldSetup.spawnFood(this, eatenFoodCounter); //dodawanie jedzenia na mapę
            eatenFoodCounter = 0; //zerowanie licznika
        }

        for (WorldObject object : currentObjects) {  //iteracja po kopii animals - żeby nie było problemów później bo niektóre rzeczy usuwamy itp to sie rozwali inaczej
            object.update(this); // aktualizuje stan zwierzęcia

            if (simulationEnded) {return;} //koniec

            if (object instanceof Animal animal && !animal.isAlive()) { //jeżeli to zwierze i nie żyje
                SimulationStatsManager.writeToFile(animal.getName() + "," + animal.getId() + ",is dead\n");
                removeObject(animal); //usuwa je
            }
        }

        //sprawdzenie czy ktoś jeszzcze zyje
        boolean anyoneAlive = false;
        for (Animal animal : animals) {
            if (animal.isAlive()) {
                anyoneAlive = true; //jeśli chociaz 1 żyje do symulacja trwa
                break;
            }
        }
        if (!anyoneAlive) { //jeśli nie to się kończy
            endSimulation();
            SimulationStatsManager.writeToFile("notification,Simulation ended because all animals are dead\n");
        }
    }


    /**
     * Forces the simulation to end.
     */
    public void endSimulation() {
        simulationEnded = true;
    }



    private boolean tralaleroSpawned = false;  //encapsulation


    /**
     * Attempts to spawn the legendary boss entity "TralaleroTralala" with a 0.5% probability.
     */
    private void addTralaleroTralala() {
        if (!tralaleroSpawned && random.nextInt(200) == 0) { // 0.5% szans
            tralaleroSpawned = true;

            //tworzenie listy wolnych pól na mapie które nie są rafą
            List<Coord> freeCoords = new ArrayList<>();
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    Coord coord = new Coord(x, y);
                    Tile tile = getTile(coord);
                    if (tile != null && !isOccupied(coord) && tile.getMapType() != MapType.CORAL) {
                        freeCoords.add(coord);
                    }
                }
            }

            //dodawanie bossa jeśli jest miejsce
            if (!freeCoords.isEmpty()) {
                int randomId = World.random.nextInt(freeCoords.size()); //losuje którą pozycje wziac z dostepnych pól - int od 0 do size-1
                Coord chosenCoord = freeCoords.get(randomId);
                Animal tralaleroTralala = WorldSetup.createAnimalFromName("TralaleroTralala", chosenCoord); //tworzenie bossa
                if (tralaleroTralala != null) { //jeśli się stworzył
                    addObject(tralaleroTralala);
                    SimulationStatsManager.writeToFile("TralaleroTralala," + tralaleroTralala.getId() + ",appeared\n");
                }
            } else {
                SimulationStatsManager.writeToFile("\nnotification,Boss wanted to appear but no free space on the map for him\n");
            }
        }
    }



    /**
     * Returns a list of animals near the specified coordinates within a given radius.
     * @param coord  The central coordinate.
     * @param radius The radius to check for nearby animals.
     * @return List of nearby animals.
     */
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


    /**
     * Retrieves the tile at the given coordinates, ensuring it's within bounds.
     * @param coord The coordinate to check.
     * @return The tile at the given location, or null if out of bounds.
     */
    public Tile getTile(Coord coord) {
        return inBounds(coord) ? grid[coord.getX()][coord.getY()] : null;
    }


    /**
     * Checks if a coordinate is within the world boundaries.
     * @param coord The coordinate to check.
     * @return True if within bounds, otherwise false.
     */
    public boolean inBounds(Coord coord) {
        return coord.getX() >= 0 && coord.getX() < width && coord.getY() >= 0 && coord.getY() < height;
    }

    /**
     * Determines if a tile is occupied by an animal.
     * @param coord The coordinate to check.
     * @return True if occupied, otherwise false.
     */
    public boolean isOccupied(Coord coord) { return !getNearbyAnimals(coord, 0).isEmpty(); }


    /**
     * Adds an object to the world.
     * If the object is an alive animal, it's also added to the animal list.
     * @param object The object to add.
     */
    public void addObject(WorldObject object) {
        if (object == null) { return; }

        objects.add(object); //wszystkie
        if (object instanceof Animal animal && animal.isAlive()) {  //tylko zwierzeta
            animals.add(animal);
        }
    }


    /**
     * Removes an object from the world.
     * If the object is a dead animal, it's removed from both the animal list and the object list.
     * @param object The object to remove.
     */
    public void removeObject(WorldObject object) {
        if (object instanceof Animal animal && !animal.isAlive()) { //tylko zwierzeta
            animals.remove(animal);
            objects.remove(object);
        } else { objects.remove(object); } //wszystko
    }


    //dodaje objekty Egg
    public void addEgg(Egg egg) {
        addObject(egg);
    }


    public List<Coord> getCoralReefCenter() { return coralReefCenter; }
    public void addCoralReefCenter(Coord coord) { coralReefCenter.add(coord); }
}