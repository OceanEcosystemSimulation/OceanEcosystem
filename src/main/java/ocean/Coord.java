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

    //losuje nowe sąsiednie współrzędne (ofc w granicach świata)
    public Coord randomAdjacent(int width, int height) {
        int nx = Math.max(0, Math.min(width - 1, x + random.nextInt(3) - 1));
        int ny = Math.max(0, Math.min(height - 1, y + random.nextInt(3) - 1));
        return new Coord(nx, ny);
    }
}

