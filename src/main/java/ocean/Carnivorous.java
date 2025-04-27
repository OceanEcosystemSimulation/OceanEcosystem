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



    public boolean attack(Animal prey, World world) {
        double attackerSpeed = this.getEffectiveSpeed(); //predator speed
        double preySpeed = prey.getEffectiveSpeed(); //prey speed

        //próba ucieczki ofiary
        if (attackerSpeed < preySpeed*1.2) { //liczba do zmiany można
            prey.escape(world);
            return false; //ucieczka udana - brak walki
        }

        //walka
        double attackerPower = this.getCombatPower();
        double preyPower = prey.getCombatPower();

        int rounds = 2; //maksymalnie 2 wymiany ciosów - do możliwej zmiany
        for (int i = 0; i < rounds; i++) {
            prey.takeDamage(attackerPower);
            if (!prey.isAlive()) return true; //prey padł

            this.takeDamage(preyPower);
            if (!this.isAlive()) return false; //predator padł
        }

        //jeśli po 2 rundach nikt nie padł
        if (this.getCombatPower() > prey.getCombatPower()) { //kto ucieka (przegryw - słabszy)
            prey.escape(world);
        } else {
            this.escape(world);
        }
        return false; //nikt nie został zabity w walce
    }
}

