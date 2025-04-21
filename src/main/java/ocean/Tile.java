package ocean;

//reprezentacja pól mapy
public class Tile {
    public int x, y; //współrzędne
    public MapType type; //typ pola
    public FoodType foodType = FoodType.NONE; //typ jedzenia, domyślnie brak jedzenia

    public Tile(int x, int y, MapType type) {
        this.x = x;
        this.y = y;
        this.type = type; //ustawia typ
    }


    //sprawdza czy jest jedzenie
    public boolean hasFood() {
        return foodType != FoodType.NONE;
    }

    //usuwanie jedzenia
    public void clearFood() {
        foodType = FoodType.NONE;
    }
}

