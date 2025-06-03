package extendedMechanics;

import body.*;
import map.Coord;
import map.MapType;
import map.Tile;
import ocean.SimulationStatsManager;
import ocean.World;
import allAnimals.Egg;

import java.util.ArrayList;
import java.util.List;


/**
 * Handles reproduction mechanics, including pregnancy conditions, birth and safe area checks.
 */
public class Reproduction {
    public static final int MINIMAL_AGE_TO_GET_PREGNANT = 18;
    public static final double ENERGY_NEEDED = 0.8; // 80% * MaxEnergy
    public static final int PREGNANCY_DURATION = 6; // 6 MONTHS - 6 TURNS // skócona ciążą

    /* -------------------------------CONDITIONS------------------------------- */

    /**
     * Checks if an animal is ready for reproduction.
     * Must be alive, old enough, not already pregnant, and have sufficient energy.
     * @param animal The animal to check.
     * @return True if ready, otherwise false.
     */
    public static boolean IsReady(Animal animal) {
        return
                animal.isAlive()
                && animal.getAge() >= MINIMAL_AGE_TO_GET_PREGNANT
                && !animal.isPregnant()
                && animal.getEnergy() >= animal.getGenes().getMaxEnergy() * ENERGY_NEEDED;
    }

    /* -------------------------------DISTANCE NEEDED------------------------------- */

    /**
     * Checks if two animals are within one tile of each other.
     * @param animal1 First animal.
     * @param animal2 Second animal.
     * @return True if they are adjacent, otherwise false.
     */
    public static boolean isDistanceOne(Animal animal1, Animal animal2) {
        Coord pos1 = animal1.getPosition();
        Coord pos2 = animal2.getPosition();
        return pos1.distance(pos2) <= 1;
    }


    /**
     * Moves the animal to an adjacent tile near its mate.
     * @param self The animal moving.
     * @param mate The mate to approach.
     * @param world The simulation world in which it happens.
     * @return True if movement was successful, otherwise false.
     */
    public static boolean moveToMate(Animal self, Animal mate, World world) {
        List<Coord> neighbour = Coord.getAdjacentCoords(mate.getPosition()); //tworzy liste pól wokół mate
        for (Coord coord : neighbour) { //iteruje po każdnym
            if (world.inBounds(coord) && !world.isOccupied(coord)) { //jeżeli jest w granicach i nie zajęte
                self.setPosition(coord);
                return true;
            }
        }
        return false;
    }


    /* -------------------------------MECHANICS------------------------------- */

    /**
     * Handles the reproduction process if the two animals meet all conditions.
     * Updates energy levels and starts pregnancy.
     * @param world The simulation world in which it happens.
     * @param animal1 First animal.
     * @param animal2 Second animal.
     * @param genes Genes to inherit.
     */
    public static void ReproductionProcess(World world, Animal animal1, Animal animal2, Genes genes) {
        //funkcja sprawdza podstawowe warunki, by stwierdzić, czy może dojść do reprodukcji
        // PŁEĆ I GATUNEK
        if (animal2 == null) {return;} // brak partnera/partnerki
        if (!animal1.getClass().equals(animal2.getClass())) {return;} // ten sam gatunek
        if (animal1.getGender() == animal2.getGender()) {return;} // ta sama płeć
        if (animal1.getId() == animal2.getId()) {return;}

        Animal female; // kobietka
        if (animal1.getGender() == Gender.FEMALE) {
            female = animal1;
        } else {
            female = animal2;
        }

        Animal male; // mężczyzna
        if (female == animal1) {
            male = animal2;
        } else {
            male = animal1;
        }

        // DYSTANS - odległość dwóch organizmów
        if(!isDistanceOne(animal1, animal2)) {return;}

        // Bezpieczna strefa
        if (!isAreaSafe(world, animal1, genes) || !isAreaSafe(world, animal2, genes)) {return;}

        //CZY SPEŁNIAJĄ WARUNKI
        if (!IsReady(female) || !IsReady(male)) {return;} //odpowiedni wiek, poziom energii, status życia i status ciąży (TAK LUB NIE)

        // SPADEK ENERGII W ZWIĄZKU Z ZAPŁODNIENIEM
        int energy_loss_male = 5;
        int energy_loss_female = 10;

        male.setEnergy(Math.max(0, male.getEnergy() - energy_loss_male));
        female.setEnergy(Math.max(0, female.getEnergy() - energy_loss_female));

        // ZMIANA STATUSU - zaczęcie się ciąży
        female.setPregnant(true);
        female.setFatherDuringPregnancy(male);
        female.setPregnancyCounter(PREGNANCY_DURATION);

        SimulationStatsManager.writeToFile(female.getName() + "," + female.getId() + ",get pregnant with," + male.getName() + "," + male.getId() + "\n");
    }

    /* -------------------------------PREGNANCY------------------------------- */

    /**
     * Decreases the pregnancy counter each turn.
     * When the counter reaches zero, the animal gives birth.
     * @param world The simulation world in which it happens.
     * @param animal The pregnant animal.
     * @return True if pregnancy is active, otherwise false.
     */
    public static boolean pregnancyTick(World world, Animal animal) {
        // funkcja zapobiega podwójnej lub miliardowej ciąży, sprawdza czy organizm jest w ciąży, dodaje licznik
        // z biegiem tur go dekrementuje
        if (animal.getGender() != Gender.FEMALE || !animal.isPregnant()) {return false;} // czy kobietka i czy w ciąży obecnie
        animal.setPregnancyCounter(animal.getPregnancyCounter() - 1); // odejmowanie 'miesiąca' co turę

        if (animal.getPregnancyCounter() == 0) { // czas rozwiązania
            spawnBaby(world, animal, animal.getFatherDuringPregnancy());
            animal.setPregnant(false);
        }
        return true;
    }

    /* -------------------------------BIRTH------------------------------- */

    /**
     * Spawns an egg that hatches after five turns, inheriting genetic traits from both parents.
     * @param world The simulation world in which it happens.
     * @param mother The mother animal.
     * @param father The father animal.
     */
    public static void spawnBaby(World world, Animal mother, Animal father) {
        // funkcja dodaje jajko, z którego przy zadanym czasie (u nas po 5 turach) wykluje się z niego młody organizm
        // logika niezbyt spójna z faktami, ale dla wizualizacji przyjęłyśmy, że tak jest
        Coord spawn = findFreeTile(world, mother.getPosition());
        if (spawn == null) {return;} // idk, nie zrespi się

        Genes genes = Genes.inherit(mother.getGenes(), father.getGenes());
        Egg egg = new Egg(spawn, genes, 5); // 5 tur
        egg.setMother(mother);
        egg.setName(mother.getName());
        world.addEgg(egg);
        SimulationStatsManager.writeToFile(egg.getName() + "," + egg.getId() + ",was born\n");
    }

    /* -------------------------------SAFE SPACE------------------------------- */

    /**
     * Checks if the area around an animal is safe for reproduction.
     * If a predator is nearby, reproduction is blocked.
     * @param world The simulation world in which it happens.
     * @param animal The animal attempting to reproduce.
     * @param genes The animal's genes.
     * @return True if reproduction can occur, otherwise false.
     */
    public static boolean isAreaSafe(World world, Animal animal, Genes genes) {
        // funkcja sprawdza, czy "na wysokości wzorku ryby", ogólniej mówiąc w zadanym promieniu znajduje się drapieżnik
        // jeśli się znajduje, rozmnażanie jest niemożliwe
        Tile tile = world.getTile(animal.getPosition());
        if (tile!=null && tile.getMapType() == MapType.CORAL) {
            return true;
        }

        int safetyRadius = genes.getSpeed(); // ile widzi organizm

        Coord position = animal.getPosition();

        List<Animal> nearby = world.getNearbyAnimals(position, safetyRadius);
        for (Animal other : nearby) {
            if (other instanceof Carnivorous || other instanceof Omnivorous && other.isAlive() && other.getId() != animal.getId()) {
                if (!other.getName().equals(animal.getName())) { // drapieżnik, wszystkożerca TEGO SAMEGO GATUNKU
                    // to było koniecznie, bo w przeciwym wypadku blokowało rozmnażanie drapieżnikom
                    return false; // w poblizu jest drapieznik
                }
            }
        }
        return true; // mogą się rozmnażać - brak drapieżników
    }

    /**
     * Finds an unoccupied tile near the given position for laying an egg.
     * @param world The simulation world in which it happens.
     * @param pos The position to search around.
     * @return A free tile for the egg or null if none is found.
     */
    public static Coord findFreeTile(World world, Coord pos) {
        int[][] directions = {{1,0}, {-1,0}, {0,1}, {0,-1}}; // kierunki: prawo, lewo, góra, dół

        List<Coord> possibleTiles = new ArrayList<>(); //lista pól, na których może złożyć jajo
        for (int[] d: directions) {
            Coord coord = new Coord(pos.getX() + d[0], pos.getY() + d[1]); //pobiera aktualne wolne miejsca
            if (world.inBounds(coord) && !world.isOccupied(coord)) { // jeśli znajduje się w granicach świata
                possibleTiles.add(coord); // dodaje do listy
            }
        }

        if (possibleTiles.isEmpty()) {return null;} //jeśli nie ma wolnego miejsca w liście - zwraca null, bo nie ma gdzie postawić

        return possibleTiles.get(World.random.nextInt(possibleTiles.size())); //wybiera losowe pole z tych, znajdujących się w liście
    }
}
