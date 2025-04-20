package ocean;

//reprezentacja pól mapy
public class Tile {
    public int x, y; //współrzędne
    public MapType type; //typ pola
    public boolean food; //czy jest na nim jedzenie

    public Tile(int x, int y, MapType type) {
        this.x = x;
        this.y = y;
        this.type = type; //ustawia typ
        this.food = false; //początkowo brak, rozkładane w World->spawnFood
    }
}

