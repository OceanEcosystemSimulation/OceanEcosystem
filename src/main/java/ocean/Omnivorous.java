package ocean;

// DO ZMIANY CAŁE - i to mocno bo pogubiłam się co i jak z nimi
public abstract class Omnivorous extends Animal implements IEat, IFight {
    public Omnivorous(Coord position) {
        super(position);
    }

    @Override
    public boolean canEat(Tile tile) { //whatever
        return tile.foodType == FoodType.PLANKTON || tile.foodType == FoodType.ALGAE;
    }

    @Override
    public void eat(Tile tile) { //whatever - do zmiany i tak
        int gain = switch (tile.foodType) {
            case PLANKTON -> 10;
            case ALGAE -> 15;
            default -> 0;
        };
        if (getAge()+gain <= 100){
            setFoodLevel(getFoodLevel() + gain);
            tile.clearFood();
        }
    }


    // do omnivore jakiegoś trzeba dać to samo w canAttack co w np Shark jest, znaczy tego typu

    // do zrobienia wszystko, atak, eat itd

}

