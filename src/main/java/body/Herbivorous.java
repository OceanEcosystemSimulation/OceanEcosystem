package body;

import extendedMechanics.Reproduction;
import map.*;
import movement.*;
import ocean.World;
import ocean.WorldSearch;

import static body.AnimalCombatUtils.randomMove;

//abstract class; inheritance from Animal and implements interfaces IEat, IMove, IMate
/**
 * Abstract class representing herbivorous animals in the world.
 * Implements basic behavior such as movement, eating, and mating.
 */
public abstract class Herbivorous extends Animal implements IEat, IMove, IMate {
    public Herbivorous(Coord position, Genes genes) {
        super(position, genes);
    }

    public Herbivorous(Coord position, Animal parent1, Animal parent2) {
        super(position, parent1, parent2);
    }


    @Override
    public boolean canEat(Tile tile) {  //polymorphism
        return tile.getFoodType() == FoodType.PLANKTON || tile.getFoodType() == FoodType.ALGAE;  //co mogą jeść
    }


    /**
     * Moves the animal on the map.
     * If hungry, it searches for food and moves toward it. Otherwise, it moves randomly.
     * @param world The world in which the animal exists.
     */
    @Override
    public void move(World world) {  //polymorphism
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


    /**
     * Attempts to find and mate with a nearby compatible animal.
     * If a mate is found within range and adjacent, reproduction occurs.
     * Otherwise, the animal moves toward the mate and tries again.
     * @param world The simulation world in which it happens.
     */
    @Override
    public void tryToMate(World world) { //implementation
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


