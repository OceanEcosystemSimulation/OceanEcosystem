package ocean;

import allAnimals.Fish;

import static ocean.Coord.meetingAtMiddle;

//abstract bo nie ma update
public abstract class Carnivorous extends Animal implements IFight, IMove, IEat {
    public Carnivorous(Coord position, Genes genes, int maxAge, int maxLoneliness) {
        super(position, genes, maxAge, maxLoneliness);
    }

    public Carnivorous(Coord position, Animal parent1, Animal parent2) {
        super(position, parent1, parent2);  //konstruktor dziecka
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
        Coord newPos = getPosition().randomAdjacent(world.getWidth(), world.getHeight(), getGenes().getSpeed(), world); //generuje nową losową pozycję sąsiednią
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


    @Override
    public boolean canEat(Tile tile) { //also przykładowe
        return getFoodLevel() <= 30 &&
                (tile.foodType == FoodType.PLANKTON || tile.foodType == FoodType.ALGAE);
    }


    //sprawdzenie czy na obecnym kafelku znajduje się jedzenie
    public void tryToEat(World world) {
        if (isAlive()) {
            Tile currentTile = world.getTile(getPosition());
            if (currentTile != null && canEat(currentTile)) { //jeśli tile zawiera jedzenie i Shark może je jeść
                eat(currentTile); //je
            }
        }
    }


    //szuka partnera
    public void tryToMate(World world) {
        if (isAlive()) {
            Animal mate = WorldSearch.nearestMate(world, this.getPosition(), this.getGenes().getSpeed(), this); //znajduje mate
            if (mate!=null && mate.getGender()!=this.getGender()) { //przeciwna płeć
                Coord meetingPointA = meetingAtMiddle(world.getWidth(), world.getHeight(), this.getPosition(), mate.getPosition());
                Coord meetingPointB = meetingAtMiddle(world.getWidth(), world.getHeight(), this.getPosition(), mate.getPosition());
                this.setPosition(meetingPointA); //skok do A
                mate.setPosition(meetingPointB); //skok do B

                if (canReproduce() && mate.canReproduce()) {
                    System.out.println(this.getName() + " id: " + this.getId() + "  reproduce with  " + mate.getName() + " id: " + mate.getId());
                    //losowanie nowych współrzędnych w zasięgu jednej kratki od aktualnej pozycji rodzica
                    Coord childPosition = this.getPosition().randomAdjacent(world.getWidth(), world.getHeight(), 1, world);

                    Animal child = new Fish(childPosition, this, mate); //tworzenie nowego dzieciaka
                    world.addAnimal(child); //dodanie dzieciaka do świata
                    System.out.println(child.getName() + " id: " + child.getId() + "  was born");
                }
            }
        }
    }
}

