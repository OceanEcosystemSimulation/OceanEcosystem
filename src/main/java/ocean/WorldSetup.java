package ocean;

import allAnimals.*;

import java.util.Random;

public class WorldSetup {
    private Random random = new Random();

    //inicjuje pola mapy jako NORMAL i losuje miejsca raf
    protected void initTiles(World world, int noCoral) {
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
    protected void spawnAnimals(World world, int count) {
        RangeOfRarity rarityRange = new RangeOfRarity();
        DrawningAnimalsByTheirRarity animalDrawer = new DrawningAnimalsByTheirRarity();

        for (int i = 0; i < count; i++) {
            Rarity rarity = rarityRange.animalsDrawingByRarity(); //losuje rarity
            String animalType = animalDrawer.drawnAnimalByRarity(rarity); //losuje typ zwierzęcia z tej klasy rarity

            Coord coord = randomCoord(world); //losuje pozycję

            Animal animal = createAnimalFromName(animalType, coord); //tworzy zwierzę
            if (animal != null) {
                world.addAnimal(animal);
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
    protected void spawnFood(World world, int noFood) {
        for (int i = 0; i < noFood; i++) {
            Coord coord = randomCoord(world); //generuje losowe współrzędne
            Tile tile = world.getTile(coord); //pobiera dane pole
            if (tile != null && !tile.hasFood()) {
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
    private Coord randomCoord(World world) {
        return new Coord(random.nextInt(world.getWidth()), random.nextInt(world.getHeight()));
    }
}
