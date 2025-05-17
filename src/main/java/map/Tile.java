package map;

import ocean.World;

//reprezentacja pól mapy
public class Tile {
    private final int x, y; //współrzędne
    private MapType mapType; //typ pola
    public FoodType foodType = FoodType.NONE; //typ jedzenia, domyślnie brak jedzenia

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
    public void clearFood(World world) {
        foodType = FoodType.NONE;
        world.setEatenFoodCounter(world.getEatenFoodCounter() + 1);
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public MapType getMapType() { return mapType; }
    public FoodType getFoodType() { return foodType; }

    public void setFoodType(FoodType foodType) { this.foodType = foodType; }
    public void setMapType(MapType mapType) { this.mapType = mapType; }
}

