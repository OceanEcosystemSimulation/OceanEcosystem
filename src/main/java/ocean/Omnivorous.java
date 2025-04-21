package ocean;

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
        //btw znalazłam właśnie w książce taki switch case, smart XD więc jak wolicie - ale i tak trzeba zmienic zeby nie jadło jak przekracza
        int gain = switch (tile.foodType) {
            case PLANKTON -> 10;
            case ALGAE -> 15;
            default -> 0;
        };
        foodLevel = Math.min(100, foodLevel + gain);
        tile.clearFood();
    }


    // do omnivora jakiegoś trzeba dać to samo co w canAttack w np Sharku

    @Override
    public boolean attack(Animal other) {
        int attackerScore = genes.strength + genes.speed;
        int defenderScore = other.genes.strength + other.genes.speed;

        if (attackerScore >= defenderScore) {
            other.alive = false;
            foodLevel = Math.min(100, foodLevel + 40);
            return true;
        } else {
            genes.strength = Math.max(genes.strength - 1, 1);
            return false;
        }
    }
}

