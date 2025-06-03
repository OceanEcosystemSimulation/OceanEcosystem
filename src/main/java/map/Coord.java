package map;

import ocean.World;

import java.util.*;


/**
 * Represents a coordinate in a two-dimensional space.
 */
public class Coord {
    private int x, y;  //encapsulation
    public static int allAtempts = 0;

    public Coord(int x, int y) {
        this.x = x;
        this.y = y;
    }


    /**
     * Calculates the distance to another coordinate using the Chebyshev metric.
     * @param other The target coordinate.
     * @return The maximum difference in the x or y axis.
     */
    public int distance(Coord other) {
        int dx = Math.abs(this.x - other.x);
        int dy = Math.abs(this.y - other.y);
        return Math.max(dx, dy);  // max( |x1 - x2|; |y1 - y2| )
    }


    /**
     * Generates a new random adjacent coordinate within a movement range (±speed), ensuring it remains within the map's boundaries.
     * If the new coordinate is already occupied, it continues attempting to find a valid position (if it's possible).
     * @param width  The width of the map.
     * @param height The height of the map.
     * @param speed  The movement speed determining range.
     * @param world  The simulation world in which it happens.
     * @return New coordinate within the movement range, or null if too many failed attempts.
     */
    public Coord randomAdjacent(int width, int height, int speed, World world) {
        Coord newCoord;
        int attempts = 0;

        do {
            int moveX = World.random.nextInt(speed * 2 + 1) - speed; //od 0 do speed*2 i odejmując speed daje to +- speed
            int moveY = World.random.nextInt(speed * 2 + 1) - speed;

            int newX = Math.max(0, Math.min(width - 1, x + moveX)); //w granicach mapy (nie mniejsze od 0 i nie wieksze od granicy)
            int newY = Math.max(0, Math.min(height - 1, y + moveY)); //albo granica albo wartość losowana

            newCoord = new Coord(newX, newY);

            attempts++;
            if (attempts >= 1000) { //kiedy przy X próbach nie będzie miejsca
                allAtempts++;
                if (allAtempts > world.getAnimals().size()/2) {
                    return null;
                } else {
                    return new Coord(x, y);
                }
            }
        } while (world.isOccupied(newCoord));
        return newCoord;
    }


    /**
     * Returns a list of all adjacent coordinates surrounding the given coordinate.
     * @param coord The central coordinate.
     * @return A list of adjacent coordinates.
     */
    public static List<Coord> getAdjacentCoords(Coord coord) {
        int x = coord.getX(); //aktualne x
        int y = coord.getY(); //aktualne y
        List<Coord> neighbors = new ArrayList<>(); //lista do przechowywania pól sąsiednich

        for (int dx = -1; dx <= 1; dx++) { //przejscie przez wszystkie sasiednie
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue; //pomija (0,0) czyli srodek
                neighbors.add(new Coord(x + dx, y + dy));
            }
        }
        return neighbors; //zwraca liste pól dookoła
    }


    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
}

