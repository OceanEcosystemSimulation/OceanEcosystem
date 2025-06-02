package body;

import extendedMechanics.Reproduction;
import map.*;
import movement.*;
import ocean.SimulationStatsManager;
import ocean.World;
import ocean.WorldSearch;

import static body.AnimalCombatUtils.randomMove;


public abstract class Omnivorous extends Animal implements IEat, IFight, IMate, IMove {
    public Omnivorous(Coord position, Genes genes) {
        super(position, genes);
    }

    public Omnivorous(Coord position, Animal parent1, Animal parent2) {
        super(position, parent1, parent2);
    }

    @Override
    public boolean canEat(Tile tile) {
        return getFoodLevel() <= 70 &&
                (tile.getFoodType() == FoodType.ALGAE || tile.getFoodType() == FoodType.PLANKTON);
    }

    //mechanika ruchu
    @Override
    public void move(World world) {
        if (getFoodLevel() < 70) {
            //szuka ofiary
            Coord preyPos = WorldSearch.nearestPrey(world, getPosition(), getGenes().getSpeed(), this);
            if (preyPos != null) {
                setPosition(preyPos);
                return;
            }

            //jak nie ma w poblizu ofiary to szuka roslinek i poziom jedzenia jest nizszy niz 30
            if (getFoodLevel() < 30) {
                Tile foodTile = WorldSearch.nearestFood(world, getPosition(), getGenes().getSpeed());
                if (foodTile != null) {
                    Coord foodPos = new Coord(foodTile.getX(), foodTile.getY());
                    if (!world.isOccupied(foodPos)) {
                        setPosition(foodPos);
                        return;
                    }
                }
            }
        }

        //nie potrzebuje jesc
        randomMove(world, this);
    }

    //mechanika ataku
    @Override
    public boolean attack(Animal prey, World world) {
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

