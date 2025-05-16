package body;


import map.*;
import movement.*;
import ocean.World;
import ocean.WorldSearch;

import static body.AnimalCombatUtils.randomMove;


//abstract bo nie ma dalej update
public abstract class Herbivorous extends Animal implements IEat, IMove {
    public Herbivorous(Coord position, Genes genes) {
        super(position, genes);
    }

    public Herbivorous(Coord position, Animal parent1, Animal parent2) {
        super(position, parent1, parent2);
    }


    @Override
    public boolean canEat(Tile tile) { //whatever, przykład, chociaż tak bym zostawiła bo to herbivore
        return tile.getFoodType() == FoodType.PLANKTON || tile.getFoodType() == FoodType.ALGAE;
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
                Coord foodPos = new Coord(foodTile.getX(), foodTile.getY());
                setPosition(foodPos); //skok do jedzenia
                return;
            }
        } else {
            randomMove(world, this); //randomowo gdy nie głodny lub brak jedzenia
        }
        System.out.println(this.getName() + " id: " + this.getId() + "  jumped to [" + this.getPosition().x + "," + this.getPosition().y + "]");
    }
}


