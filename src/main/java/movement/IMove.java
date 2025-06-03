package movement;

import ocean.World;

// move - interface
/**
 * Interface for movement behavior.
 */
public interface IMove {
    /**
     * Moves animal to a new position in the world
     * @param world The simulation world in which it happens.
     */
    void move(World world);
}
