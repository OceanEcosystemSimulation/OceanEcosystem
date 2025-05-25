package ocean;

import allAnimals.*;
import body.*;
import extendedMechanics.*;
import javafx.scene.image.Image;
import map.Coord;
import map.FoodType;
import map.MapType;
import map.Tile;

import java.util.Objects;
import java.util.Random;

import static com.sun.javafx.scene.control.skin.Utils.getResource;

public class WorldSetup {
    private static Random random = new Random();

    /* -------------------------------RAFY KORALOWE------------------------------- */

    //inicjuje pola mapy jako NORMAL i losuje miejsca raf
    static void initTiles(World world, int noCoral) {
        Tile[][] grid = world.getGrid();
        int width = world.getWidth();
        int height = world.getHeight();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid[x][y] = new Tile(x, y, MapType.NORMAL); //ustawia pole na NORMAL
            }
        }

        //zastanawiam się, czy powinien być warunek sprawdzający, czy mapa nie jest mniejsza niż wymiary rafy

        int coralreef_counter = 0, countOfReefs = noCoral;

        while (coralreef_counter <= countOfReefs) {

            int cx = random.nextInt(width); //losuje centralne pole x rafy
            int cy = random.nextInt(height); //losuje centralne pole y rafy

            //sprawdza czy w zasięgu mapy
            if (!world.inBounds(cx - 1, cy - 1) || !world.inBounds(cx + 1, cy + 1)) continue;

            // simple rozmieszczenie raf (otoczenie 3x3 na razie - jeśli dobrze ustawiłąm lol) //dobrze
            boolean IsEmpty = true; // sprawdza, czy jest wolna przestrzeń, na postawienie rafy
            for (int dx = -1; dx <= 1 && IsEmpty; dx++)
                for (int dy = -1; dy <= 1; dy++)
                    if (grid[cx + dx][cy + dy].getMapType() == MapType.CORAL) { // sprawdza, czy pole to CORAL
                        IsEmpty = false;
                    }

            if (!IsEmpty) continue; // pomija, jeśli pole jest zajęte

            // dodanie rafy koralowej na 9 polach
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    grid[cx + dx][cy + dy].setMapType(MapType.CORAL);
                }
            }

            world.addCoralReefCenter(new Coord(cx, cy)); //pobiera współrzędne środka mapy
            coralreef_counter++; //inkrementuje licznik raf
        }

        // informacja zwrotna odnośnie ilości raf
        // przypadek, w którym nie uda się wstawić tyle raf ile zostało zadane
        /*if (coralreef_counter < countOfReefs) {
            //TODO: ERROR
        }*/
    }
    //TODO: rozwiązać problem z pętlą nieskończoną

    /* -------------------------------ZWIERZĘTA------------------------------- */

    //dodaje okreslona liczbe dowolnych (rarity) zwierząt do listy w losowych pozycjach
    static void spawnAnimals(World world, int count) {
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
    static Animal createAnimalFromName(String name, Coord position) {
        //wywalało żółty że nie == i faktycznie chyba bo == poówbuje chyba adresy a equals wartości wieć zmieniam
        if (name.equals("Nemo")) {
            return new Nemo(position);
        } else if (name.equals("Shark")) {
            return new Shark(position);
        }else if (name.equals("Orca")) {
            return new Orca(position);
        }else if (name.equals("OceanicPuffer")) {
            return new OceanicPuffer(position);
        }
        else if (name.equals("Whale")) {
            return new Whale(position);
        }
        //itd
        else {
            return null;  //na wypadek błędu
        }
    }

    /* -------------------------------JEDZENIE------------------------------- */

    //losowo rozmieszcza jedzenie
    static void spawnFood(World world, int noFood) {
        int maxNoFood = Math.min(noFood, world.getHeight()*world.getWidth()); //jeśli count jest wiecej niz pól dodaje ile może

        for (int i = 0; i < maxNoFood; i++) {
            Tile tile;
            do {
                Coord coord = randomCoord(world); //generuje losowe współrzędne
                tile = world.getTile(coord); //pobiera dane pole
            } while (tile.hasFood()); //losuje dopóki wylosowane bedzie wolne

            //losowanie typu jedzenia na kafelku
            int foodTypeRoll = random.nextInt(2); //losuje wartość (0,1)
            FoodType foodType = switch (foodTypeRoll) {
                case 0 -> FoodType.PLANKTON;
                case 1 -> FoodType.ALGAE;
                default -> FoodType.NONE;
            };
            tile.setFoodType(foodType);
        }
    }

    /* -------------------------------KOORDYNATY------------------------------- */

    //generuje losowe współrzedne Coord na swiecie - w granicach ofc bo random.nextInt(bound) zawsze zwraca liczbę w zakresie [0, bound)
    private static Coord randomCoord(World world) {
        return new Coord(random.nextInt(world.getWidth()), random.nextInt(world.getHeight()));
    }
}
