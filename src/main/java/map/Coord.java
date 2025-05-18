package map;

import ocean.World;

import java.util.Random;
import java.util.*;


// reprezentacja współrzędnych
public class Coord {
    public int x, y;
    private static Random random = new Random();

    //inicjalizacja współrzędnych
    public Coord(int x, int y) {
        this.x = x;
        this.y = y;
    }


    //odległość - wykorzystuje metrykę maksimum (Czebyszewa) - max różnica w osi x lub y
    public int distance(Coord other) {
        int dx = Math.abs(this.x - other.x);
        int dy = Math.abs(this.y - other.y);
        return Math.max(dx, dy);  // max( |x1 - x2|; |y1 - y2| )
    }


    //losuje nowe współrzędne od -speed do +speed (ofc w granicach świata) - jesli juz ktos tam jest to losuje dalej
    public Coord randomAdjacent(int width, int height, int speed, World world) {
        Coord newCoord;
        do {
            int moveX = random.nextInt(speed * 2 + 1) - speed; //od 0 do speed*2 i odejmując speed daje to +- speed
            int moveY = random.nextInt(speed * 2 + 1) - speed;

            int newX = Math.max(0, Math.min(width - 1, x + moveX)); //w granicach mapy (nie mniejsze od 0 i nie wieksze od granicy)
            int newY = Math.max(0, Math.min(height - 1, y + moveY)); //albo granica albo wartość losowana

            newCoord = new Coord(newX, newY);
        } while (world.isOccupied(newCoord));
        return newCoord;
    }


    //zwraca sąsiednie pola wokół coord
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

