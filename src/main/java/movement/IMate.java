package movement;

import ocean.World;

// mate - interface
/**
 * Interface for mating behavior.
 */
public interface IMate {
    /**
     * Attempts to fina a suitable mate and mate.
     * @param world The simulation world in which it happens.
     */
    void tryToMate(World world);
}

