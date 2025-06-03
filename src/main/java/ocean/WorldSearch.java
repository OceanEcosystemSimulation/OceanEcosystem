package ocean;

import body.*;
import map.Coord;
import map.MapType;
import map.Tile;
import movement.IFight;

import java.util.List;

/**
 * Provides various search methods for locating food, prey, mates, and coral tiles within the world.
 */
public class WorldSearch {

    /**
     * Finds the nearest food source (algae or plankton) within the given radius.
     * Searches for tiles containing food that are not occupied.
     * @param world The simulation world in which it happens.
     * @param position The central position.
     * @param radius The search radius.
     * @return The nearest tile containing food, or null if there is none found.
     */
    public static Tile nearestFood(World world, Coord position, int radius) {
        Tile nearestFood = null; //zmenna z najblizszym jedzeniem
        int minDist = Integer.MAX_VALUE; //najmniejsza odległość od pozycji tego co je

        for (int dx=-radius; dx<=radius; dx++) { //iteracja po promieniu
            for (int dy=-radius; dy<=radius; dy++) {
                Coord candidate = new Coord(position.getX() + dx, position.getY() + dy); //wspołrzedne kandydata na ofiarę
                if (world.inBounds(candidate)) { //czy współrzędne w granicach mapy
                    Tile tile = world.getTile(candidate); //pobranie pola
                    if (tile != null && tile.hasFood() && !world.isOccupied(candidate)) { //sprawdza czy jest na nim jedzenie i czy ktos tam jest
                        int dist = position.distance(candidate); //odległość od candidate
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



    /**
     * Searches for the nearest prey within the specified radius.
     * Ensures can attack it and it's not near a whale.
     * @param world The world in which the animals exists.
     * @param position The position of the predator.
     * @param radius The search radius.
     * @param predator The predator performing this search.
     * @return The coordinates of the nearest prey, or null if there is none found.
     */
    public static Coord nearestPrey(World world, Coord position, int radius, IFight predator) {
        Animal nearestPrey = null; //zmenna z najblizszą ofiarą
        int minDist = Integer.MAX_VALUE; //najmniejsza odległość od pozycji

        for (Animal animal : world.getNearbyAnimals(position, radius)) { //iteracja po liście zwierząt w promieniu
            Tile preyTile = world.getTile(animal.getPosition()); //pole na którym jest ofiara
            if (preyTile!=null && preyTile.getMapType()!= MapType.CORAL && predator.canAttack(animal) && !isWhaleNearby(world, animal)) { //sprawdzenie czy może zaatakować i czy to nie rafa
                int dist = position.distance(animal.getPosition()); //odległość od prey
                if (dist < minDist) { //jeśli bliżej to bierze
                    minDist = dist;
                    nearestPrey = animal;
                }
            }
        }
        return nearestPrey!=null ? nearestPrey.getPosition() : null; //zwraca współrzędne ofiary lub null jak jej nie ma
    }


    /**
     * Finds the nearest mating partner of the same species within the given radius.
     * @param world The world in which the animals exists.
     * @param position The position of the searcher.
     * @param radius The search radius.
     * @param self The animal searching for a mate.
     * @return The nearest compatible mate, or null if there is none found.
     */
    public static Animal nearestMate(World world, Coord position, int radius, Animal self) {
        Animal nearestMate = null; //zmenna z najblizszym mate
        int minDist = Integer.MAX_VALUE; //najmniejsza odległość od pozycji

        for (Animal animal : world.getNearbyAnimals(position, radius)) { //iteracja po liście zwierząt w promieniu
            if (animal!=null && animal!=self && animal.getName() != null && self.getName() != null && animal.getName().equals(self.getName())) { //porównywanie gatunków
                int dist = position.distance(animal.getPosition()); //odległość od mate
                if (dist < minDist) {
                    minDist = dist;
                    nearestMate = animal;
                }
            }
        }
        return nearestMate; //zwraca mate
    }


    /**
     * Finds the nearest coral reef tile within the specified radius.
     * @param world The simulation world in which it happens.
     * @param position The central position.
     * @param radius The search radius.
     * @return The coordinates of the nearest coral reef tile, or null if there is none found.
     */
    public static Coord nearestCoral(World world, Coord position, int radius) {
        Coord nearestCoral = null; //zmienna do przechowywania coords tego pola
        int minDist = Integer.MAX_VALUE; //najmniejsza odległość od pozycji

        for (int dx=-radius; dx<=radius; dx++) {  //iteracja po polach w promieniu
            for (int dy=-radius; dy<=radius; dy++) {
                Coord newPos = new Coord(position.getX() + dx, position.getY() + dy); //wspołrzedne kandydata
                if (world.inBounds(newPos)) {   //czy w granicach
                    Tile tile = world.getTile(newPos);  //pobranie pola
                    if (tile!=null && tile.getMapType()==MapType.CORAL && !world.isOccupied(newPos)) { //sprawdza czy to jest coral i czy ktoś tam jest
                        int dist = position.distance(newPos);  //odległość od pola coral
                        if (dist < minDist) {
                            minDist = dist;
                            nearestCoral = new Coord(newPos.getX(), newPos.getY());
                        }
                    }
                }
            }
        }
        return nearestCoral; //zwaca najbliższe pole z coral
    }


    /**
     * Checks if a whale is located near the given animal.
     * @param world The world in which the animals exists.
     * @param animal The animal to check if it's close to Whale.
     * @return True if a Whale is nearby, otherwise false.
     */
    public static boolean isWhaleNearby(World world, Animal animal) {
        for (Coord neighbor : Coord.getAdjacentCoords(animal.getPosition())) { //przeszukuje najblizsze pola zwierzaka
            if (world.inBounds(neighbor)) {
                List<Animal> nearbyAnimals = world.getNearbyAnimals(neighbor, 0);
                for (Animal nearbyAnimal : nearbyAnimals) {
                    if (nearbyAnimal.getName() != null && nearbyAnimal.getName().equals("Whale")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
