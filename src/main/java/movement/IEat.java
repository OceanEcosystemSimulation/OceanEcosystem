package movement;

import body.Animal;
import map.Tile;
import ocean.World;

public interface IEat {
    boolean canEat(Tile tile);
    void eat(Tile tile, World world);

    default void tryToEat(World world, Animal self) {
        if (self.isAlive()) {
            Tile currentTile = world.getTile(self.getPosition()); //pobiera pole na którym znajduje się ryba
            if (currentTile!=null && currentTile.hasFood() && canEat(currentTile)) { //sprawdza czy jest jedzenie (na wszelki?) i czy zwierze może je zjeść
                eat(currentTile, world); //wywołanie mechaniki jedzenia
            }
        }
    }
}

