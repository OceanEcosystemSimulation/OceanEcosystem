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

    //myślę że ruch bedą mieli taki sam, idą do roślinek - jak coś to się przeniesie
    //chociaż ruch do zmiany bo aktualnie idzie losowo a raczej da mu się half losowo half do jedzenia w zależności od poziomu głodu
    @Override
    public void move(World world) {
        Coord newPos = position.randomAdjacent(world.getWidth(), world.getHeight());
        position = newPos; //update do nowych koordynatów
    }
}

