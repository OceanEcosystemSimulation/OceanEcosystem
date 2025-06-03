package ocean;

import allAnimals.*;
import body.*;
import extendedMechanics.*;
import map.Coord;
import map.FoodType;
import map.MapType;
import map.Tile;

/**
 * Handles the initial setup of the simulation world, including terrain, animals, and food distribution.
 */
public class WorldSetup {

    /* -------------------------------RAFY KORALOWE------------------------------- */

    /**
     * Initializes the map tiles and randomly generates coral reefs.
     * @param world The simulation world in which it happens.
     * @param noCoral The number of coral reefs to generate.
     */
    static void initTiles(World world, int noCoral) {
        Tile[][] grid = world.getGrid();
        int width = world.getWidth();
        int height = world.getHeight();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid[x][y] = new Tile(x, y, MapType.NORMAL); //ustawia pole na NORMAL
            }
        }

        int maxCountOfCorals = maxCountOnMap(width, height);

        int coralGeneratingLimit = (int) (height * 0.35); // max wysokość, by nie tworzyły się na grafice wody // zakres

        // sprawdza, czy jest w stanie w ogóle wygenerować rafę
        if (width < 3 || height < 3) {
            System.out.println("The given map size is too small, it is not possible to place any coral reefs.\n");
            return;
        }

        if (noCoral > maxCountOfCorals) {
            System.out.println("The given map size is too small, it is not possible to place so many coral reefs.\n");
            SimulationStatsManager.writeToFile("\nnotification,Successfully added only " + maxCountOfCorals + " from " + noCoral + " coral reefs\n");
            noCoral = maxCountOfCorals;
        }
        int coralreef_counter = 0;
        int countOfAttempts = 0;
        int MaxCountOfAttempts = 1000;

        while (coralreef_counter < noCoral && countOfAttempts < MaxCountOfAttempts) {
            countOfAttempts++;

            int cx = World.random.nextInt(width); //losuje centralne pole x rafy
            int cy = coralGeneratingLimit + World.random.nextInt(height - coralGeneratingLimit); //losuje centralne pole y rafy //UPDATE: rafy mają
            // ograniczony zakres wysokości, przez co nie tworzą się na wodzie
            //sprawdza czy w zasięgu mapy
            Coord topLeft = new Coord(cx - 1, cy - 1);
            Coord bottomRight = new Coord(cx + 1, cy + 1);
            if (!world.inBounds(topLeft) || !world.inBounds(bottomRight)) {continue;}

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


    /**
     * Calculates the maximum number of coral reefs possible based on map size.
     * @param width The map width.
     * @param height The map height.
     * @return The maximum number of coral reefs that can be placed.
     */
    static int maxCountOnMap(int width, int height) {
        int maxHeight = (int) (height * 0.35);
        int spawningAreaHeight = height - maxHeight;

        int tilesX = (width - 2) / 3; // -2 - uwzględniam krawędzie, bo wtedy rafa się nie zmieści
        int tilesY = (spawningAreaHeight - 2) / 3; // /3, poniewaz nie może być np. rozciągnięte w pionie/poziomie, tylko 3x3
        // mam nadzieję, że dobra logika :C
        return tilesX * tilesY;
    }



    /* -------------------------------ZWIERZĘTA------------------------------- */


    /**
     * Randomly spawns animals in the simulation world based on rarity.
     * @param world The simulation world in which it happens.
     * @param count The number of animals to spawn.
     */
    static void spawnAnimals(World world, int count) {
        int added = 0; //ile zwierzat dodano pomyślnie
        int maxCount = Math.min(count, world.getHeight() * world.getWidth()); //jeśli count jest wiecej niz pól dodaje ile może
        RangeOfRarity rarityRange = new RangeOfRarity();
        DrawningAnimalsByTheirRarity animalDrawer = new DrawningAnimalsByTheirRarity();

        for (int i = 0; i < maxCount; i++) {
            Rarity rarity = rarityRange.animalsDrawingByRarity(); //losuje rarity
            String animalType = animalDrawer.drawnAnimalByRarity(rarity); //losuje typ zwierzęcia z tej klasy rarity

            Coord coord;
                    do { coord = randomCoord(world); }  //losuje pozycje
                    while (world.isOccupied(coord)); //losuje dopóki wylosowane bedzie wolne

            Animal animal = createAnimalFromName(animalType, coord); //tworzy zwierzę
            if (animal != null) {
                world.addObject(animal);
                added++;
            }
        }

        if (maxCount < count) { //info
            System.out.println("The given map size is too small, it is not possible to place so many animals.\n");
            SimulationStatsManager.writeToFile("\nnotification,Successfully added only " + added + " from " + count + " animals\n");
        }
    }


    /**
     * Creates an animal instance based on its type name.
     * @param name The name of the animal type.
     * @param position The position where the animal should be created.
     * @return A new animal instance, or null if invalid.
     */
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
            case "Turtle" -> new Turtle(position);
            case "Crab" -> new Crab(position);
            case "Shrimp" -> new Shrimp(position);
            case "Star" -> new Star(position);
            case "TralaleroTralala" -> new TralaleroTralala(position);
            default -> null;  //na wypadek błędu
        };
    }



    /* -------------------------------JEDZENIE------------------------------- */

    /**
     * Spawns food sources at random locations in the world.
     * @param world The simulation world in which it happens.
     * @param noFood The number of food sources to generate.
     */
    static void spawnFood(World world, int noFood) {
        int maxNoFood = Math.min(noFood, world.getHeight() * world.getWidth()); //jeśli count jest wiecej niz pól dodaje ile może
        int added = 0; //ile pomyślnie dodano

        for (int i = 0; i < maxNoFood; i++) {
            Tile tile;
            do {
                Coord coord = randomCoord(world); //generuje losowe współrzędne
                tile = world.getTile(coord); //pobiera dane pole
            } while (tile.hasFood()); //losuje dopóki wylosowane bedzie wolne

            //losowanie typu jedzenia na kafelku
            int foodTypeRoll = World.random.nextInt(2); //losuje wartość (0,1)
            FoodType foodType = switch (foodTypeRoll) {
                case 0 -> FoodType.PLANKTON;
                case 1 -> FoodType.ALGAE;
                default -> FoodType.NONE;
            };
            tile.setFoodType(foodType);
            added++;
        }

        if (maxNoFood < noFood) { //info
            System.out.println("The given map size is too small, it is not possible to place so many food.\n");
            SimulationStatsManager.writeToFile("\nnotification,Successfully added only " + added + " from " + noFood + " food\n");
        }
    }



    /* -------------------------------KOORDYNATY------------------------------- */

    //generuje losowe współrzedne Coord na swiecie - w granicach ofc bo random.nextInt(bound) zawsze zwraca liczbę w zakresie [0, bound)
    /**
     * Generates random coordinates within the world boundaries.
     * @param world  The simulation world in which it happens.
     * @return A randomly generated coordinate.
     */
    static Coord randomCoord(World world) {
        return new Coord(World.random.nextInt(world.getWidth()), World.random.nextInt(world.getHeight()));
    }
}
