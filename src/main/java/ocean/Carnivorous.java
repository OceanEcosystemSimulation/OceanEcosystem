package ocean;

//abstract bo nie ma update
public abstract class Carnivorous extends Animal implements IFight, IMove {
    public Carnivorous(Coord position) {
        super(position);
    }


    //mechanika ruchu
    @Override
    public void move(World world) {
        if (getFoodLevel() < 70) {
            Coord preyPos = world.nearestPrey(getPosition(), getGenes().getSpeed(), this); //szuka ofiary najblizszej
            if (preyPos != null) {
                setPosition(preyPos); //skok do ofiary
                return;
            }
        }
        if (getFoodLevel() < 30) {
            Tile foodTile = world.nearestFood(getPosition(), getGenes().getSpeed()); //szuka najbliższe jedzenie
            if (foodTile != null) {
                Coord foodPos = new Coord(foodTile.x, foodTile.y);
                setPosition(foodPos); //skok do jedzenia
                return;
            }
        }
        randomMove(world); //randomowo gdy nie głodny lub brak jedzenia i ofiary
    }


    //losowy ruch w zasięgu speed
    private void randomMove(World world) {
        Coord newPos = getPosition().randomAdjacent(world.getWidth(), world.getHeight(), getGenes().getSpeed()); //generuje nową losową pozycję sąsiednią
        setPosition(newPos); //ustawia pozycję
    }


    //mechanika ataku - do zmiany
    @Override
    public boolean attack(Animal target) { //oblicza atak swój i defence celu ale to przykład więc do zmiany trch
        int attackerScore = getGenes().getStrength() + getGenes().getSpeed();
        int defenderScore = target.getGenes().getStrength() + target.getGenes().getSpeed();

        if (attackerScore >= defenderScore) {
            target.die();
            return true;
        } else {
            getGenes().setStrength(Math.max(getGenes().getStrength() - 1, 1)); //porażka i zmniejszenie siły - do zmiany
            return false;
        }
    }
}

