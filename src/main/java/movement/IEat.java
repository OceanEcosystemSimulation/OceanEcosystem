package movement;

import ocean.Tile;

public interface IEat {
    boolean canEat(Tile tile);
    void eat(Tile tile);
}

