package body;

import extendedMechanics.Reproduction;
import map.*;
import movement.*;
import ocean.World;
import ocean.WorldSearch;

import static body.AnimalCombatUtils.randomMove;

//abstract class inheriting from Animal, which is further inherited by specific animal species
//interface implementation – enforces this class (or its descendants) to implement the methods contained in these interfaces
public abstract class Herbivorous extends Animal implements IEat, IMove, IMate {
    public Herbivorous(Coord position, Genes genes) {
        super(position, genes);
    }

    public Herbivorous(Coord position, Animal parent1, Animal parent2) {
        super(position, parent1, parent2);
    }


    @Override
    public boolean canEat(Tile tile) { //co mogą jeść
        return tile.getFoodType() == FoodType.PLANKTON || tile.getFoodType() == FoodType.ALGAE;
    }


    //mechanika ruchu
    @Override
    public void move(World world) {
        if (getFoodLevel() < 70) {
            Tile foodTile = WorldSearch.nearestFood(world, getPosition(), getGenes().getSpeed()); //szuka najbliższe jedzenie
            if (foodTile != null) {
                Coord foodPos = new Coord(foodTile.getX(), foodTile.getY());
                if (!world.isOccupied(foodPos)) {
                    setPosition(foodPos); //skok do jedzenia
                    return; //zwraca by nie szło już do random move
                }
            }
        }
        randomMove(world, this); //randomowo gdy nie głodny lub brak jedzenia
    }


    @Override
    public void tryToMate(World world) {
        int range = this.getGenes().getSpeed();
        Animal mate = WorldSearch.nearestMate(world, this.getPosition(), range, this);
        if (mate != null) {
            if (Reproduction.isDistanceOne(this, mate)) { //jeżeli są w kratkach obok
                Reproduction.ReproductionProcess(world,this, mate, getGenes()); //mechanika reprodukcji
            } else {
                boolean move = Reproduction.moveToMate(this, mate, world);
                if (move && Reproduction.isDistanceOne(this, mate)) { //czy się przesunął i na wszelki czy mate jest obok
                    Reproduction.ReproductionProcess(world,this, mate, getGenes()); //mechanika reprodukcji
                }
            }
        }
    }
}


