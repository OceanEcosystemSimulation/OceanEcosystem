package ocean;

import allAnimals.*;

import java.util.Random;

public class WorldSetup {
    private static Random random = new Random();

    //inicjuje pola mapy jako NORMAL i losuje miejsca raf
    protected static void initTiles(World world, int noCoral) {
        Tile[][] grid = world.getGrid();
        int width = world.getWidth();
        int height = world.getHeight();

        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
               grid[x][y] = new Tile(x, y, MapType.NORMAL); //ustawia pole na NORMAL

        // simple rozmieszczenie raf (otoczenie 3x3 na razie - jeśli dobrze ustawiłąm lol)
        for (int i = 0; i < noCoral; i++) {
            int cx = random.nextInt(width); //losuje centralne pole x rafy
            int cy = random.nextInt(height); //losuje centralne pole y rafy
            for (int dx = -1; dx <= 1; dx++)
                for (int dy = -1; dy <= 1; dy++)
                    if (world.inBounds(cx + dx, cy + dy)) //sprawdza czy w zasięgu mapy
                        grid[cx + dx][cy + dy].type = MapType.CORAL; //ustawia pole na CORAL
        }
    }


    //dodaje okreslona liczbe dowolnych (rarity) zwierząt do listy w losowych pozycjach
    protected static void spawnAnimals(World world, int count) {
        int added = 0; //ile zwierzat dodano pomyślnie
        int maxCount = Math.min(count, world.getHeight()*world.getWidth()); //jeśli count jest wiecej niz pól dodaje ile może
        RangeOfRarity rarityRange = new RangeOfRarity();
        DrawningAnimalsByTheirRarity animalDrawer = new DrawningAnimalsByTheirRarity();

        for (int i = 0; i < maxCount; i++) {
            Rarity rarity = rarityRange.animalsDrawingByRarity(); //losuje rarity
            String animalType = animalDrawer.drawnAnimalByRarity(rarity); //losuje typ zwierzęcia z tej klasy rarity

            Coord coord;
            do {coord = randomCoord(world);}  //losuje pozycje
            while (world.isOccupied(coord)); //losuje dopóki wylosowane bedzie wolne

            Animal animal = createAnimalFromName(animalType, coord); //tworzy zwierzę
            if (animal != null) {
                world.addAnimal(animal);
                added++;
            }
        }
        System.out.println("Successfully added " + added + " from " + count + " animals");
    }


    //tworzenie zwierząt
    protected static Animal createAnimalFromName(String name, Coord position) {
        //wywalało żółty że nie == i faktycznie chyba bo == poówbuje chyba adresy a equals wartości wieć zmieniam
        if (name.equals("Nemo")) { //do zmiany w nazwach w klasie Fish albo tutaj
            return new Fish(position);
        } else if (name.equals("Shark")) {
            return new Shark(position);
        }
        //itd
        else {
            return null;  //na wypadek błędu
        }
    }


    //losowo rozmieszcza jedzenie
    protected static void spawnFood(World world, int noFood) {
        int maxNoFood = Math.min(noFood, world.getHeight()*world.getWidth()); //jeśli count jest wiecej niz pól dodaje ile może

        for (int i = 0; i < maxNoFood; i++) {
            Tile tile;
            do {
                Coord coord = randomCoord(world); //generuje losowe współrzędne
                tile = world.getTile(coord); //pobiera dane pole
            } while (tile.hasFood()); //losuje dopóki wylosowane bedzie wolne

            //losowanie typu jedzenia na kafelku
            int foodTypeRoll = random.nextInt(2); //losuje wartość (0,1)
            tile.foodType = switch (foodTypeRoll) { //dobra, wale to, coś był problem z ifem ale nie ogarniam czemu, więc zmieniam na switch case, wybaczcie
                case 0 -> FoodType.PLANKTON;
                case 1 -> FoodType.ALGAE;
                default -> FoodType.NONE;
            };
        }
    }


    //generuje losowe współrzedne Coord na swiecie - w granicach ofc bo random.nextInt(bound) zawsze zwraca liczbę w zakresie [0, bound)
    private static Coord randomCoord(World world) {
        return new Coord(random.nextInt(world.getWidth()), random.nextInt(world.getHeight()));
    }
}
