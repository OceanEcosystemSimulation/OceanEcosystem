package ocean;

import allAnimals.Fish;
import allAnimals.Shark;

import java.util.*;

public class World {
    private int width, height; //powirzchnia mapy
    private Tile[][] grid; //siatka - różne typy mapy i objekty
    private List<Animal> animals = new ArrayList<>(); //lista zwierząt na świecie
    private Random random = new Random(); //do losowań

    //rozmieszczenie pól i zwierząt (na razie zawiera liczbę turn konkretną)
    public World(int width, int height, int noFood, int noCoral, int noFish, int noSharks, int noWhales, int ticks) {
        this.width = width;
        this.height = height;
        this.grid = new Tile[width][height];
        initTiles(noCoral); //określa jakie jest dane pole mapy
        spawnAnimals(noFish, noSharks, noWhales);
        spawnFood(noFood);
    }

    //inicjuje pola mapy jako NORMAL i losuje miejsca raf
    private void initTiles(int noCoral) {
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                grid[x][y] = new Tile(x, y, MapType.NORMAL); //ustawia pole na NORMAL

        // simple rozmieszczenie raf (otoczenie 3x3 na razie - jeśli dobrze ustawiłąm lol)
        for (int i = 0; i < noCoral; i++) {
            int cx = random.nextInt(width); //losuje centralne pole x rafy
            int cy = random.nextInt(height); //losuje centralne pole y rafy
            for (int dx = -1; dx <= 1; dx++)
                for (int dy = -1; dy <= 1; dy++)
                    if (inBounds(cx + dx, cy + dy)) //sprawdza czy w zasięgu mapy
                        grid[cx + dx][cy + dy].type = MapType.CORAL; //ustawia pole na CORAL
        }
    }

    //dodaje okreslona liczbe danych zwierząt do listy w losowych pozycjach
    private void spawnAnimals(int noFish, int noSharks, int noWhales) {
        for (int i = 0; i < noFish; i++) animals.add(new Fish(randomCoord()));
        for (int i = 0; i < noSharks; i++) animals.add(new Shark(randomCoord()));
        //for (int i = 0; i < noWhales; i++) animals.add(new Whale(randomCoord()));
        //przykłady ale to idzie dalej jak więcej dodamy
    }

    //losowo rozmieszcza jedzenie
    private void spawnFood(int noFood) {
        for (int i = 0; i < noFood; i++) {
            Coord c = randomCoord(); //generuje losowe współrzędne
            Tile tile = grid[c.x][c.y]; //pobiera dane pole
            if (tile != null) {
                // Losowanie typu jedzenia na kafelku
                int foodTypeRoll = random.nextInt(3); //losuje wartość (0,1,2)
                if (foodTypeRoll==0) { //chyba bym zrobiła to switch case ale idk chyba nie ma znaczenia poza wyglądem
                    // szczerze zrobiła bym to switch case bo z if else się pruje że ==2 bedzie zawsze prawdziwe a ja nie widzę czemu
                    // jak próbowałam switch case to tak nie było więc idk gdzie problem
                    tile.foodType = FoodType.NONE;
                } else if (foodTypeRoll==1) {
                    tile.foodType = FoodType.PLANKTON;
                } else if (foodTypeRoll==2) {
                    tile.foodType = FoodType.ALGAE;
                } else {tile.foodType = FoodType.NONE;} //tak na wszelki bo nie ufam brakowi błędów
            }
        }
    }

    //generuje losowe współrzedne Coord na swiecie
    private Coord randomCoord() {
        return new Coord(random.nextInt(width), random.nextInt(height));
    }

    //główna symulacja świata - w każdym cyklu aktualizuje zwierzęta - dead alive
    public void runSimulation(int ticks) {
        for (int t = 0; t < ticks; t++) {
            List<Animal> newAnimals = new ArrayList<>(); //nowa ze zwierzętami

            //iteracja po kopii animals - żeby nie było problemów później bo niektóre rzeczy usuwamy to sie rozwali inaczej
            for (Animal a : new ArrayList<>(animals)) {
                a.update(this); //update dla każdego zwierzęcia - miało byc jako update dla zwierząt o stanie na mapie
                if (!a.alive) animals.remove(a); //usuwa martwe
            }
            animals.addAll(newAnimals); //dodaje nowe zwierzęta do animals
        }
    }

    //sprawdza czy dane pole jest w zasięgu mapy
    public boolean inBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    //zwraca komórkę jeśli jest w granicach
    public Tile getTile(Coord c) {
        return inBounds(c.x, c.y) ? grid[c.x][c.y] : null;
    }

    //zwraca listę zwierząt które znajdują się w pobliżu określonych współrzędnych Coord
    public List<Animal> getNearbyAnimals(Coord c, int radius) {
        List<Animal> result = new ArrayList<>(); //lista do przechowywania zwierzat w okolicy
        for (Animal a : animals) {
            if (a.position.distance(c) <= radius && a.alive) //odległośc między zwierżeciem a c <= promień i czy żyje
                result.add(a); //jeśli tak to dodaje do listy
        }
        return result;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
