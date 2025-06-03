package movement;

import body.Animal;
import map.Tile;
import ocean.World;

// food - interface
/**
 * Interface for food-related behaviors.
 */
public interface IEat {
    /**
     * Checks if the animal can eat from a given tile - if it's what he eats.
     * @param tile The tile to check for food.
     * @return True if food is suitable, otherwise false.
     */
    boolean canEat(Tile tile);

    /**
     * Allows animal to eat food from the given tile, increasing its food level.
     * The food level increases based on the type of food.
     * If the total remains within the limit (max 100), the action is logged, and the tile's food is removed.
     * @param tile  The tile to eat from.
     * @param world The simulation world in which it happens.
     */
    void eat(Tile tile, World world);

    /**
     * Default method for attempting to eat.
     * If the animal is alive and food is available, it consumes food from its current tile.
     * @param world The simulation world in which it happens.
     * @param self  The animal trying to eat.
     */
    default void tryToEat(World world, Animal self) {
        if (self.isAlive()) {
            Tile currentTile = world.getTile(self.getPosition()); //pobiera pole na którym znajduje się ryba
            if (currentTile!=null && currentTile.hasFood() && canEat(currentTile)) { //sprawdza czy jest jedzenie (na wszelki?) i czy zwierze może je zjeść
                eat(currentTile, world); //wywołanie mechaniki jedzenia
            }
        }
    }
}

