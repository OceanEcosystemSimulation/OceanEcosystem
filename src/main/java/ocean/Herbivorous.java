package ocean;

//abstract bo nie ma dalej update
public abstract class Herbivorous extends Animal implements IEat {
    public Herbivorous(Coord position) {
        super(position);
    }

    @Override
    public boolean canEat(Tile tile) { //whatever, przykład
        return tile.foodType == FoodType.PLANKTON || tile.foodType == FoodType.ALGAE;
    }

    @Override
    public void eat(Tile tile) { //przykładowe jak pisać
        switch (tile.foodType) { //sorry za switch case ale tak mi się to cholernie podoba zawsze że nie mogłam się oprzeć by nie użyć
            case PLANKTON -> foodLevel = Math.min(100, foodLevel + 10); //mniej daje
            case ALGAE -> foodLevel = Math.min(100, foodLevel + 20); //ryba lubi glony więc wiecej XD
        }
        tile.clearFood();
    }
}

