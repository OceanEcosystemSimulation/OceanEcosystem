package ocean;

import java.util.*;

public class World {
    private final int width, height; //powirzchnia mapy
    private Tile[][] grid; //siatka - różne typy mapy i objekty
    private List<Animal> animals = new ArrayList<>(); //lista zwierząt na świecie

    //rozmieszczenie pól i zwierząt (na razie zawiera liczbę turn konkretną)
    public World(int width, int height, int noFood, int noCoral, int noAnimals, int ticks, WorldSetup worldSetup) {
        this.width = width;
        this.height = height;
        this.grid = new Tile[width][height];
        worldSetup.initTiles(this, noCoral); //określa jakie jest dane pole mapy
        worldSetup.spawnAnimals(this, noAnimals); //spawn zwierząt randomowych według rarity
        worldSetup.spawnFood(this, noFood); //spawn jedzenie randomowo
    }


    //główna symulacja świata - w każdym cyklu aktualizuje zwierzęta - dead alive
    public void runSimulation(int ticks) {
        for (int t = 0; t < ticks; t++) {
            List<Animal> newAnimals = new ArrayList<>(); //nowa ze zwierzętami

            //iteracja po kopii animals - żeby nie było problemów później bo niektóre rzeczy usuwamy to sie rozwali inaczej
            for (Animal animal : new ArrayList<>(animals)) {
                animal.update(this); //update dla każdego zwierzęcia - miało byc jako update dla zwierząt o stanie na mapie
                if (!animal.isAlive()) animals.remove(animal); //usuwa martwe
            }
            animals.addAll(newAnimals); //dodaje nowe zwierzęta do animals
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

    //zwraca komórkę jeśli jest w granicach
    public Tile getTile(Coord coord) {
        return inBounds(coord.x, coord.y) ? grid[coord.x][coord.y] : null;
    }

    //sprawdza czy dane pole jest w zasięgu mapy
    public boolean inBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }


    //dodaje zwierzeta do listy - potrzebuje ale może póżniej się da jakoś inaczej to załatwić
    public void addAnimal(Animal animal) {
        if (animal != null && animal.isAlive()) {
            animals.add(animal); //dodaje jeśli żywe
        }
    }
}