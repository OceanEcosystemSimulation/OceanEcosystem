package movement;

import body.Animal;
import map.Tile;
import ocean.World;

// food - interface
public interface IEat {
    boolean canEat(Tile tile); // warunek, kiedy organizm może rozpocząć konspumcję
    void eat(Tile tile, World world); // definiuje jaki pokarm może jeść dany organizm

    //metoda domyślna
    //klasy implementujące ten interfejs, mogą nadpisywać tą metodę
    default void tryToEat(World world, Animal self) {
        if (self.isAlive()) {
            Tile currentTile = world.getTile(self.getPosition()); //pobiera pole na którym znajduje się ryba
            if (currentTile!=null && currentTile.hasFood() && canEat(currentTile)) { //sprawdza czy jest jedzenie (na wszelki?) i czy zwierze może je zjeść
                eat(currentTile, world); //wywołanie mechaniki jedzenia
            }
        }
    }
}

