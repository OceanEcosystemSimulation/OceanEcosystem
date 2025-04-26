package ocean;

import java.util.Random;

// reprezentacja współrzędnych
public class Coord {
    public int x, y;
    private Random random = new Random();

    //inicjalizacja współrzędnych
    public Coord(int x, int y) {
        this.x = x;
        this.y = y;
    }

    //odległość euklidesowo
    public double distance(Coord other) {
        return Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2));
    }

    //losuje nowe współrzędne od -speed do +speed (ofc w granicach świata)
    public Coord randomAdjacent(int width, int height, int speed) {
        int moveX = random.nextInt(speed * 2 + 1) - speed; //od 0 do speed*2 i odejmując speed daje to +- speed
        int moveY = random.nextInt(speed * 2 + 1) - speed;

        int newX = Math.max(0, Math.min(width - 1, x + moveX)); //w granicach mapy (nie mniejsze od 0 i nie wieksze od granicy)
        int newY = Math.max(0, Math.min(height - 1, y + moveY)); //albo granica albo wartość losowana

        return new Coord(newX, newY);
    }

}

