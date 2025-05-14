package map;

//reprezentacja pól mapy
public class Tile {
    private final int x, y; //współrzędne
    private MapType mapType; //typ pola
    private FoodType foodType = FoodType.NONE; //typ jedzenia, domyślnie brak jedzenia

    public Tile(int x, int y, MapType mapType) {
        this.x = x;
        this.y = y;
        this.mapType = mapType; //ustawia typ
    }


    //sprawdza czy jest jedzenie
    public boolean hasFood() {
        return foodType != FoodType.NONE;
    }

    //usuwanie jedzenia
    public void clearFood() {
        foodType = FoodType.NONE;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public MapType getMapType() { return mapType; }
    public FoodType getFoodType() { return foodType; }

    public void setFoodType(FoodType foodType) { this.foodType = foodType; }
    public void setMapType(MapType mapType) { this.mapType = mapType; }
}

