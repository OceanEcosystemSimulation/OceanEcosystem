package movement;

import ocean.World;

// move - interface
public interface IMove {
    /**
     * Moves animal to a new position in the world
     * @param world The simulation world in which the animal exists
     */
    void move(World world);
}
