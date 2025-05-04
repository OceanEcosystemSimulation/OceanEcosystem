package ocean;

import allAnimals.Fish;

import static ocean.Coord.meetingAtMiddle;

//abstract bo nie ma dalej update
public abstract class Herbivorous extends Animal implements IEat, IMove {
    public Herbivorous(Coord position, Genes genes, int maxAge, int maxLoneliness) {
        super(position, genes, maxAge, maxLoneliness);
    }

    public Herbivorous(Coord position, Animal parent1, Animal parent2) {
        super(position, parent1, parent2);
    }


    @Override
    public boolean canEat(Tile tile) { //whatever, przykład, chociaż tak bym zostawiła bo to herbivore
        return tile.foodType == FoodType.PLANKTON || tile.foodType == FoodType.ALGAE;
    }

    //sprawdzenie czy na obecnym kafelku znajduje się jedzenie
    protected void tryToEat(World world) {
        Tile currentTile = world.getTile(getPosition()); //pobiera pole na którym znajduje się ryba
        if (currentTile!=null && currentTile.hasFood() && canEat(currentTile)) { //sprawdza czy jest jedzenie (na wszelki?) i czy ryba może je zjeść
            eat(currentTile); //wywołanie mechaniki jedzenia
        }
    }


    //mechanika ruchu
    @Override
    public void move(World world) {
        if (getFoodLevel() < 70) {
            Tile foodTile = WorldSearch.nearestFood(world, getPosition(), getGenes().getSpeed()); //szuka najbliższe jedzenie
            if (foodTile != null) {
                Coord foodPos = new Coord(foodTile.x, foodTile.y);
                setPosition(foodPos); //skok do jedzenia
                return;
            }
        } else {
            randomMove(world); //randomowo gdy nie głodny lub brak jedzenia
        }
        System.out.println(this.getName() + " id: " + this.getId() + "  jumped to [" + this.getPosition().x + "," + this.getPosition().y + "]");
    }


    //losowy ruch w zasięgu speed
    private void randomMove(World world) {
        Coord newPos = getPosition().randomAdjacent(world.getWidth(), world.getHeight(), getGenes().getSpeed()); //generuje nową losową pozycję sąsiednią
        setPosition(newPos); //ustawia pozycję
    }


    //szuka partnera
    public void tryToMate(World world) {
        if (isAlive()) {
            Animal mate = WorldSearch.nearestMate(world, this.getPosition(), this.getGenes().getSpeed(), this); //znajduje mate
            if (mate!=null && mate.getGender()!=this.getGender()) { //przeciwna płeć
                Coord meetingPointA = meetingAtMiddle(world.getWidth(), world.getHeight(), this.getPosition(), mate.getPosition(), rand);
                Coord meetingPointB = meetingAtMiddle(world.getWidth(), world.getHeight(), this.getPosition(), mate.getPosition(), rand);
                //UWAGA!!!!!: ta metoda meetingAtMiddle jest popieprzona, coś mi się rozwaliło i robiłam cokolwiek by nie podkreślało już, ale nwm co się tu stało
                this.setPosition(meetingPointA); //skok do A
                mate.setPosition(meetingPointB); //skok do B

                if (canReproduce() && mate.canReproduce()) {
                    System.out.println(this.getName() + " id: " + this.getId() + "  reproduce with  " + mate.getName() + " id: " + mate.getId());
                    //losowanie nowych współrzędnych w zasięgu jednej kratki od aktualnej pozycji rodzica
                    Coord childPosition = this.getPosition().randomAdjacent(world.getWidth(), world.getHeight(), 1);

                    Animal child = new Fish(childPosition, this, mate); //tworzenie nowego dzieciaka
                    world.addAnimal(child); //dodanie dzieciaka do świata
                    System.out.println(child.getName() + " id: " + child.getId() + "  was born");
                }
            }
        }
    }

}


