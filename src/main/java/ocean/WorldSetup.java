package ocean;

import allAnimals.*;
import body.*;
import extendedMechanics.*;
import map.Coord;
import map.FoodType;
import map.MapType;
import map.Tile;
import java.util.Random;

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


        int maxCountOfCorals = maxCountOnMap(width, height);

        int coralGeneratingLimit = (int) (height * 0.35); // max wysokość, by nie tworzyły się na grafice wody // zakres

        if (noCoral > maxCountOfCorals) {
            System.out.println("Zadany wymiar mapy jest zbyt mały, nie można umieścić tylu raf");
            System.out.printf("Zmniejszam ilość raf, do największego możliwego... Ilość raf: %d\n", maxCountOfCorals);
            noCoral = maxCountOfCorals;
        }
        int coralreef_counter = 0;
        int countOfAttempts = 0;
        int MaxCountOfAttempts = 1000;


        while (coralreef_counter < noCoral && countOfAttempts < MaxCountOfAttempts) {
            countOfAttempts++;

            int cx = random.nextInt(width); //losuje centralne pole x rafy
            int cy = coralGeneratingLimit + random.nextInt(height - coralGeneratingLimit); //losuje centralne pole y rafy //UPDATE: rafy mają
            // ograniczony zakres wysokości, przez co nie tworzą się na wodzie
            //sprawdza czy w zasięgu mapy
            if (!world.inBounds(cx - 1, cy - 1) || !world.inBounds(cx + 1, cy + 1)) continue;

            // simple rozmieszczenie raf (otoczenie 3x3 na razie - jeśli dobrze ustawiłąm lol) //dobrze
            boolean IsEmpty = true; // sprawdza, czy jest wolna przestrzeń, na postawienie rafy
            for (int dx = -1; dx <= 1 && IsEmpty; dx++)
                for (int dy = -1; dy <= 1; dy++)
                    if (grid[cx + dx][cy + dy].getMapType() == MapType.CORAL) { // sprawdza, czy pole to CORAL
                        IsEmpty = false;
                    }
            if (IsEmpty) {
                // dodanie rafy koralowej na 9 polach
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        grid[cx + dx][cy + dy].setMapType(MapType.CORAL);
                    }
                }

                world.addCoralReefCenter(new Coord(cx, cy)); //pobiera współrzędne środka mapy
                coralreef_counter++; //inkrementuje licznik raf

            }
        }
    }

    public static int maxCountOnMap(int width, int height) {
        int maxHeight = (int) (height * 0.35);
        int spawningAreaHeight = height - maxHeight;

        int tilesX = (width - 2) / 3; // -2 - uwzględniam krawędzie, bo wtedy rafa się nie zmieści
        int tilesY = (spawningAreaHeight - 2) / 3; // /3, poniewaz nie może być np. rozciągnięte w pionie/poziomie, tylko 3x3
        // mam nadzieję, że dobra logika :C
        return tilesX * tilesY;
    }

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
                world.addObject(animal);
                added++;
            }
        }
        System.out.println("Successfully added " + added + " from " + count + " animals");
    }


    //tworzenie zwierząt
    static Animal createAnimalFromName(String name, Coord position) {
        return switch (name) {
            case "Nemo" -> new Nemo(position);
            case "Shark" -> new Shark(position);
            case "Orca" -> new Orca(position);
            case "OceanicPuffer" -> new OceanicPuffer(position);
            case "Whale" -> new Whale(position);
            case "Octopus" -> new Octopus(position);
            case "Dolphin" -> new Dolphin(position);
            case "Seal" -> new Seal(position);
            default -> null;  //na wypadek błędu
        };
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
