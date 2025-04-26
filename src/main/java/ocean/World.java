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
    public World(int width, int height, int noFood, int noCoral, int noAnimals, int ticks) {
        this.width = width;
        this.height = height;
        this.grid = new Tile[width][height];
        initTiles(noCoral); //określa jakie jest dane pole mapy
        spawnAnimals(noAnimals); //spawn zwierząt randomowych według rarity
        spawnFood(noFood); //spawn jedzenie randomowo
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


    //dodaje okreslona liczbe dowolnych (rarity) zwierząt do listy w losowych pozycjach
    private void spawnAnimals(int count) {
        RangeOfRarity rarityRange = new RangeOfRarity();
        DrawningAnimalsByTheirRarity animalDrawer = new DrawningAnimalsByTheirRarity();

        for (int i = 0; i < count; i++) {
            Rarity rarity = rarityRange.animalsDrawingByRarity(); //losuje rarity
            String animalType = animalDrawer.drawnAnimalByRarity(rarity); //losuje typ zwierzęcia z tej klasy rarity

            Coord coord = randomCoord(); //losuje pozycję

            Animal animal = createAnimalFromName(animalType, coord); //tworzy zwierzę
            if (animal != null) {
                animals.add(animal);
            }
        }
    }


    //tworzenie zwierząt
    private Animal createAnimalFromName(String name, Coord position) {
        //wywalało żółty że nie == i faktycznie chyba bo == poówbuje chyba adresy a equals wartości wieć zmieniam
        if (name.equals("Nemo")) {
            return new Fish(position);
        } else if (name.equals("Shark")) {
            return new Shark(position);
        }
        // itd
        else {
            return null;
        } // na wypadek błędu
    }


    //losowo rozmieszcza jedzenie
    private void spawnFood(int noFood) {
        for (int i = 0; i < noFood; i++) {
            Coord coord = randomCoord(); //generuje losowe współrzędne
            Tile tile = grid[coord.x][coord.y]; //pobiera dane pole
            if (tile != null) {
                // Losowanie typu jedzenia na kafelku
                int foodTypeRoll = random.nextInt(3); //losuje wartość (0,1,2)
                tile.foodType = switch (foodTypeRoll) { //dobra, wale to, coś był problem z ifem ale nie ogarniam czemu, więc zmieniam na switch case, wybaczcie
                    case 0 -> FoodType.NONE;
                    case 1 -> FoodType.PLANKTON;
                    case 2 -> FoodType.ALGAE;
                    default -> FoodType.NONE;
                };

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
            for (Animal animal : new ArrayList<>(animals)) {
                animal.update(this); //update dla każdego zwierzęcia - miało byc jako update dla zwierząt o stanie na mapie
                if (!animal.isAlive()) animals.remove(animal); //usuwa martwe
            }
            animals.addAll(newAnimals); //dodaje nowe zwierzęta do animals
        }
    }


    //sprawdza czy dane pole jest w zasięgu mapy
    public boolean inBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }


    //zwraca komórkę jeśli jest w granicach
    public Tile getTile(Coord coord) {
        return inBounds(coord.x, coord.y) ? grid[coord.x][coord.y] : null;
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


    //znajdywanie alg lub planktonu w okreslonym promieniu
    public Tile nearestFood(Coord position, int radius) {
        Tile nearestFood = null; //zmenna z najblizszym jedzeniem
        int minDist = Integer.MAX_VALUE; //najmniejsza odległość od pozycji tego co je

        for (int dx=-radius; dx<=radius; dx++) { //iteracja po promieniu
            for (int dy=-radius; dy<=radius; dy++) {
                Coord candidate = new Coord(position.x + dx, position.y + dy); //wspołrzedne kandydata na ofiarę
                if (inBounds(candidate.x, candidate.y)) { //czy współrzędne w granicach mapy
                    Tile tile = getTile(candidate); //pobranie pola
                    if (tile != null && tile.hasFood()) { //sprawdza czy jest na nim jedzenie
                        int dist = (int) position.distance(candidate); //rzutowanie double z euklidesa na int
                        if (dist < minDist) { //jeżeli ma mniejszy dystans niż wcześniejsze to bierze to
                            minDist = dist;
                            nearestFood = tile;
                        }
                    }
                }
            }
        }
        return nearestFood; //zwaca najbliższe pole z jedzeniem
    }


    //szuka najbliższego organizmu do zjedzenia (ofiarę)
    public Coord nearestPrey(Coord position, int radius, Carnivorous predator) {
        Animal nearestPrey = null; //zmenna z najblizszą ofiarą
        int minDist = Integer.MAX_VALUE; //najmniejsza odległość od pozycji

        for (Animal animal : getNearbyAnimals(position, radius)) { //iteracja po liście zwierząt w promieniu
            if (predator.canAttack(animal)) { //sprawdzenie czy może zaatakować
                int dist = (int) position.distance(animal.getPosition());
                if (dist < minDist) { //jeśli bliżej to bierze
                    minDist = dist;
                    nearestPrey = animal;
                }
            }
        }
        return nearestPrey != null ? nearestPrey.getPosition() : null; //zwraca współrzędne ofiary lub null jak jej nie ma
    }
}