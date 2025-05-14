package movement;

import map.Tile;

public interface IEat {
    boolean canEat(Tile tile);
    void eat(Tile tile);
}

