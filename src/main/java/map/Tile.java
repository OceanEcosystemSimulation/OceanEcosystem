package map;

import ocean.World;

/**
 * Represents a tile on the map.
 */
public class Tile {
    //encapsulation
    private final int x, y; //współrzędne
    private MapType mapType; //typ pola
    private FoodType foodType = FoodType.NONE; //typ jedzenia, domyślnie brak jedzenia  //composition

    public Tile(int x, int y, MapType mapType) {
        this.x = x;
        this.y = y;
        this.mapType = mapType; //ustawia typ
    }


    /**
     * Checks if the tile contains food (plankton or algae).
     * @return True if there is food on the tile, otherwise false.
     */
    public boolean hasFood() {
        return foodType != FoodType.NONE;
    }


    /**
     * Removes food from the tile and updates the world's food consumption statistics.
     * @param world The simulation world in which it happens.
     */
    public void clearFood(World world) {
        foodType = FoodType.NONE;
        world.setEatenFoodCounter(world.getEatenFoodCounter() + 1);
        world.totalEatenFood++;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public MapType getMapType() { return mapType; }
    public FoodType getFoodType() { return foodType; }

    public void setFoodType(FoodType foodType) { this.foodType = foodType; }
    public void setMapType(MapType mapType) { this.mapType = mapType; }
}

