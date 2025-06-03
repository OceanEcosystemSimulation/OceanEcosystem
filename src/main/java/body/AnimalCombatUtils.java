package body;

import map.*;
import ocean.SimulationStatsManager;
import ocean.World;
import ocean.WorldSearch;

/**
 * AnimalCombatUtils class handles animal combat-related logic in the simulation.
 */
public class AnimalCombatUtils {
    /**
     * Calculates the effective strength of an animal based on its genes and current energy level.
     * @param animal The animal whose effective strength is to be calculated.
     * @return The effective strength value
     */
    static double getEffectiveStrength(Animal animal) {
        return animal.getGenes().getStrength() * (animal.getEnergy()/100.0);
    }

    /**
     * Calculates the effective speed of an animal based on its genes and current energy level.
     * @param animal The animal whose effective speed is to be calculated.
     * @return The effective speed value.
     */
    static double getEffectiveSpeed(Animal animal) {
        return animal.getGenes().getSpeed() * (animal.getEnergy()/100.0);
    }

    /**
     * Calculates the overall combat power of an animal using  strength and speed.
     * @param animal The animal whose combat power is to be calculated.
     * @return The combat power value.
     */
    public static double getCombatPower(Animal animal) {
        return getEffectiveStrength(animal) * 0.7 + getEffectiveSpeed(animal) * 0.3;
    }


    /**
     * Applies damage to an animal, reducing its health. If health drops to zero or below, the animal dies.
     * @param world  The world in which the animal exists.
     * @param animal The animal taking damage.
     * @param amount The amount of damage to apply.
     */
    static void takeDamage(World world, Animal animal, double amount) {
        int newHealth = (int)(animal.getHealth() - amount);
        animal.setHealth(Math.max(newHealth, 0));

        if (animal.getHealth() <= 0) {
            animal.die(world);
        }
    }


    /**
     * Attempts to make the animal escape from combat.
     * If a coral reef is within range, the animal jumps to it.
     * Otherwise, it tries to escape in a random direction up to its maximum speed.
     * @param world  The world in which the animal exists.
     * @param animal The animal attempting to escape.
     */
    public static void escape(World world, Animal animal) {
        Coord pos = animal.getPosition();
        int distance = animal.getGenes().getSpeed();

        Coord coralPos = WorldSearch.nearestCoral(world, pos, distance);
        if (coralPos!=null) { //gdy istnieje rafa w zasięgu
            animal.setPosition(coralPos); //skok na rafę
        } else { //gdy nie ma rafy to losowy kierunek ucieczki na pełną odległość dlatego nie randomMove
            int dx = World.random.nextBoolean() ? distance : -distance;
            int dy = World.random.nextBoolean() ? distance : -distance;
            Coord escapePos = new Coord(pos.getX() + dx, pos.getY() + dy);

            //jesli w granicach i nikogo tam nie ma to skacze na pełną odl w innym wypadku losowo
            if (world.inBounds(escapePos) && !world.isOccupied(escapePos)) {
                animal.setPosition(escapePos);
            } else {
                randomMove(world, animal);
            }
        }
    }


    /**
     * Moves the animal to a random adjacent position within its speed range.
     * If no valid position is found, the simulation ends.
     * @param world  The world in which the animal exists.
     * @param animal The animal to move.
     */
    public static void randomMove(World world, Animal animal) {
        Coord newPos = animal.getPosition().randomAdjacent(world.getWidth(), world.getHeight(), animal.getGenes().getSpeed(), world); //generuje nową losową pozycję sąsiednią
        if (newPos == null) { //jeśli zwróciło null czyli nie ma już miejcsc
            world.endSimulation(); //kończy symulację
            SimulationStatsManager.writeToFile("\nnotification,Simulation ended because map is full\n");
            return;
        }
        animal.setPosition(newPos); //ustawia pozycję
    }
}
