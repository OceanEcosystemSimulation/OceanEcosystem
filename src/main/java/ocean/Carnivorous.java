package ocean;

//abstract bo nie ma update
public abstract class Carnivorous extends Animal implements IFight, IMove {
    public Carnivorous(Coord position, Genes genes, int maxAge, int maxLoneliness) {
        super(position, genes, maxAge, maxLoneliness);
    }


    //mechanika ruchu
    @Override
    public void move(World world) {
        if (getFoodLevel() < 70) {
            Coord preyPos = WorldSearch.nearestPrey(world, getPosition(), getGenes().getSpeed(), this); //szuka ofiary najblizszej
            if (preyPos != null) {
                setPosition(preyPos); //skok do ofiary
                return;
            }
        } else if (getFoodLevel() < 30) {
            Tile foodTile = WorldSearch.nearestFood(world, getPosition(), getGenes().getSpeed()); //szuka najbliższe jedzenie
            if (foodTile != null) {
                Coord foodPos = new Coord(foodTile.x, foodTile.y);
                setPosition(foodPos); //skok do jedzenia
                return;
            }
        } else {
            randomMove(world); //randomowo gdy nie głodny lub brak jedzenia i ofiary
        }
        System.out.println(this.getName() + " id: " + this.getId() + "  jumped to [" + this.getPosition().x + "," + this.getPosition().y + "]");
    }


    //losowy ruch w zasięgu speed
    private void randomMove(World world) {
        Coord newPos = getPosition().randomAdjacent(world.getWidth(), world.getHeight(), getGenes().getSpeed()); //generuje nową losową pozycję sąsiednią
        setPosition(newPos); //ustawia pozycję
    }



    public boolean attack(Animal prey, World world) {
        double attackerSpeed = AnimalCombatUtils.getEffectiveSpeed(this); //predator speed
        double preySpeed = AnimalCombatUtils.getEffectiveSpeed(prey); //prey speed

        //próba ucieczki ofiary
        if (attackerSpeed < preySpeed*1.2) { //liczba do zmiany można
            AnimalCombatUtils.escape(world, prey);
            System.out.println(prey.getName() + " id: " + prey.getId() + "  escape from  " + this.getName() + " id: " + this.getId());
            return false; //ucieczka udana - brak walki
        }

        //walka
        double attackerPower = AnimalCombatUtils.getCombatPower(this);
        double preyPower = AnimalCombatUtils.getCombatPower(prey);

        int rounds = 2; //maksymalnie 2 wymiany ciosów - do możliwej zmiany
        for (int i = 0; i < rounds; i++) {
            AnimalCombatUtils.takeDamage(prey, attackerPower);
            if (!prey.isAlive()) {
                System.out.println(this.getName() + " id: " + this.getId() + "  killed " + prey.getName() + " id: " + prey.getId());
                return true; //prey padł
            }

            AnimalCombatUtils.takeDamage(this, preyPower);
            if (!this.isAlive()) {
                System.out.println(prey.getName() + " id: " + prey.getId() + "  killed " + this.getName() + " id: " + this.getId());
                return false; //predator padł
            }
        }

        //jeśli po 2 rundach nikt nie padł
        if (AnimalCombatUtils.getCombatPower(this) > AnimalCombatUtils.getCombatPower(prey)) { //kto ucieka (przegryw - słabszy)
            AnimalCombatUtils.escape(world, prey);
            System.out.println(prey.getName() + " id: " + prey.getId() + "  escape from  " + this.getName() + " id: " + this.getId() + "  after 2 turns");
        } else {
            AnimalCombatUtils.escape(world, this);
            System.out.println(this.getName() + " id: " + this.getId() + "  escape from  " + prey.getName() + " id: " + prey.getId() + "  after 2 turns");
        }
        return false; //nikt nie został zabity w walce
    }
}

