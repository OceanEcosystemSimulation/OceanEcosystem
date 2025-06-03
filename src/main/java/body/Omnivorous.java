package body;

import extendedMechanics.Reproduction;
import map.*;
import movement.*;
import ocean.SimulationStatsManager;
import ocean.World;
import ocean.WorldSearch;

import static body.AnimalCombatUtils.randomMove;

//abstract class; inheritance from Animal and implements interfaces IEat, IFight, IMove, IMate
/**
 * Abstract class representing an omnivorous animal in the simulation.
 * Omnivores can eat both plant-based food and attack other animals.
 * Implements movement, eating, mating, and combat behavior.
 */
public abstract class Omnivorous extends Animal implements IEat, IFight, IMate, IMove {
    public Omnivorous(Coord position, Genes genes) {
        super(position, genes);
    }

    public Omnivorous(Coord position, Animal parent1, Animal parent2) {
        super(position, parent1, parent2);
    }

    @Override
    public boolean canEat(Tile tile) {  //polymorphism
        return getFoodLevel() <= 70 &&
                (tile.getFoodType() == FoodType.ALGAE || tile.getFoodType() == FoodType.PLANKTON);
    }


    /**
     * If the animal is hungry (food level below 70), it searches for the nearest prey and moves toward it.
     * If no prey is found and food level is critically low (below 30),it looks for the nearest available food tile and moves toward it.
     * If neither prey nor food is available, or the animal is not hungry, it performs a random movement.
     * @param world The simulation world in which the animal exists.
     */
    @Override
    public void move(World world) {  //polymorphism
        if (getFoodLevel() < 70) {
            Coord preyPos = WorldSearch.nearestPrey(world, getPosition(), getGenes().getSpeed(), this); //szuka ofiary najblizszej
            if (preyPos != null) {
                setPosition(preyPos); //skok do ofiary
                return;
            } else if (getFoodLevel() < 30) { //jeżeli nie ma ofiary i jest super głodny
                Tile foodTile = WorldSearch.nearestFood(world, getPosition(), getGenes().getSpeed()); //szuka najbliższe jedzenie
                if (foodTile != null) {
                    Coord foodPos = new Coord(foodTile.getX(), foodTile.getY());
                    if (!world.isOccupied(foodPos)) { //jeśli miejsce wolne
                        setPosition(foodPos); //skok do jedzenia
                        return;
                    }
                }
            }
        }
        randomMove(world, this); //randomowo gdy nie głodny lub brak jedzenia i ofiary
    }


    /**
     * Attempts to attack a prey animal.
     * If the prey is faster, it escapes. Otherwise, a combat sequence occurs (max 5 rounds).
     * The outcome depends on combat power and may result in death or escape.
     * @param prey The animal being attacked.
     * @param world The simulation world in which it happens.
     * @return True if the prey is killed, otherwise false.
     */
    @Override
    public boolean attack(Animal prey, World world) {  //implementation
        double attackerSpeed = AnimalCombatUtils.getEffectiveSpeed(this);
        double preySpeed = AnimalCombatUtils.getEffectiveSpeed(prey);

        if (attackerSpeed < preySpeed) {
            AnimalCombatUtils.escape(world, prey);
            SimulationStatsManager.writeToFile(prey.getName() + "," + prey.getId() + ",escaped from," + this.getName() + "," + this.getId() + "\n");
            return false;
        }//ofiara jest szybsza - ucieka drapieznikowi


        double attackerPower = AnimalCombatUtils.getCombatPower(this);
        double preyPower = AnimalCombatUtils.getCombatPower(prey);

        int rounds = 5;
        for (int i = 0; i < rounds; i++) { //walka
            AnimalCombatUtils.takeDamage(world, prey, attackerPower);
            if (!prey.isAlive()) { //ofiara zginela
                SimulationStatsManager.writeToFile(this.getName() + "," + this.getId() + ",killed," + prey.getName() + "," + prey.getId() + "\n");
                return true;
            }

            AnimalCombatUtils.takeDamage(world,this, preyPower);
            if (!this.isAlive()) { //drapieznik zginal
                SimulationStatsManager.writeToFile(prey.getName() + "," + prey.getId() + ",killed," + this.getName() + "," + this.getId() + "\n");
                return false;
            }
        }

        if (attackerPower > preyPower) { //ucieczka
            AnimalCombatUtils.escape(world, prey);
            SimulationStatsManager.writeToFile(prey.getName() + "," + prey.getId() + ",escaped from," + this.getName() + "," + this.getId() + "\n");
        } else {
            AnimalCombatUtils.escape(world, this);
            SimulationStatsManager.writeToFile(this.getName() + "," + this.getId() + ",escaped from," + prey.getName() + "," + prey.getId() + "\n");
        }

        return false;
    }


    /**
     * Attempts to find and mate with a nearby compatible animal.
     * If a mate is found within range and adjacent, reproduction occurs.
     * Otherwise, the animal moves toward the mate and tries again.
     * @param world The simulation world in which it happens.
     */
    public void tryToMate(World world) {  //implementation
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

