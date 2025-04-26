package ocean;

//abstract bo nie ma dalej update
public abstract class Herbivorous extends Animal implements IEat, IMove {
    public Herbivorous(Coord position) {
        super(position);
    }

    @Override
    public boolean canEat(Tile tile) { //whatever, przykład, chociaż tak bym zostawiła bo to herbivore
        return tile.foodType == FoodType.PLANKTON || tile.foodType == FoodType.ALGAE;
    }


    //mechanika ruchu
    @Override
    public void move(World world) {
        if (getFoodLevel() < 70) {
            Tile foodTile = world.nearestFood(getPosition(), getGenes().getSpeed()); //szuka najbliższe jedzenie
            if (foodTile != null) {
                Coord foodPos = new Coord(foodTile.x, foodTile.y);
                setPosition(foodPos); //skok do jedzenia
                return;
            }
        }
        randomMove(world); //randomowo gdy nie głodny lub brak jedzenia
    }


    //losowy ruch w zasięgu speed
    private void randomMove(World world) {
        Coord newPos = getPosition().randomAdjacent(world.getWidth(), world.getHeight(), getGenes().getSpeed()); //generuje nową losową pozycję sąsiednią
        setPosition(newPos); //ustawia pozycję
    }

}


