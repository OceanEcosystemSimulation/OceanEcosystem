package body;

import extendedMechanics.Reproduction;
import map.*;
import movement.*;
import ocean.SimulationStatsManager;
import ocean.World;
import ocean.WorldSearch;

import static body.AnimalCombatUtils.randomMove;

//abstract class; inheritance from Animal and implements interfaces IFight, IMove, IMate
public abstract class Carnivorous extends Animal implements IFight, IMove, IMate {
    public Carnivorous(Coord position, Genes genes) {
        super(position, genes);
    }

    public Carnivorous(Coord position, Animal parent1, Animal parent2) {
        super(position, parent1, parent2);
    }


    //mechanika ruchu
    @Override
    public void move(World world) {  //polymorphism
        if (getFoodLevel() < 70) {
            Coord preyPos = WorldSearch.nearestPrey(world, getPosition(), getGenes().getSpeed(), this); //szuka ofiary najblizszej
            if (preyPos != null) {
                setPosition(preyPos); //skok do ofiary
                return;
            }
        }
        randomMove(world, this); //randomowo gdy nie głodny lub brak jedzenia i ofiary
    }


    //machanika ataku
    public boolean attack(Animal prey, World world) {  //implementation
        double attackerSpeed = AnimalCombatUtils.getEffectiveSpeed(this); //predator speed
        double preySpeed = AnimalCombatUtils.getEffectiveSpeed(prey); //prey speed

        //próba ucieczki ofiary
        if (attackerSpeed < preySpeed) {
            AnimalCombatUtils.escape(world, prey);
            SimulationStatsManager.writeToFile(prey.getName() + "," + prey.getId() + ",escape from," + this.getName() + "," + this.getId() + "\n");
            return false; //ucieczka udana - brak walki
        }

        //walka
        double attackerPower = AnimalCombatUtils.getCombatPower(this);
        double preyPower = AnimalCombatUtils.getCombatPower(prey);

        int rounds = 5; //maksymalnie X wymian ciosów
        for (int i = 0; i < rounds; i++) {
            AnimalCombatUtils.takeDamage(world, prey, attackerPower);
            if (!prey.isAlive()) {
                SimulationStatsManager.writeToFile(this.getName() + "," + this.getId() + ",killed," + prey.getName() + "," + prey.getId() + "\n");
                return true; //prey padł
            }

            AnimalCombatUtils.takeDamage(world,this, preyPower);
            if (!this.isAlive()) {
                SimulationStatsManager.writeToFile(prey.getName() + "," + prey.getId() + ",killed," + this.getName() + "," + this.getId() + "\n");
                return false; //predator padł
            }
        }

        //jeśli po 2 rundach nikt nie padł
        if (AnimalCombatUtils.getCombatPower(this) > AnimalCombatUtils.getCombatPower(prey)) { //kto ucieka (przegryw - słabszy)
            AnimalCombatUtils.escape(world, prey);
            SimulationStatsManager.writeToFile(prey.getName() + "," + prey.getId() + ",escape from," + this.getName() + "," + this.getId() + ",after " + rounds + " turns\n");
        } else {
            AnimalCombatUtils.escape(world, this);
            SimulationStatsManager.writeToFile(this.getName() + "," + this.getId() + ",escape from," + prey.getName() + "," + prey.getId() + ",after " + rounds + " turns\n");
        }
        return false; //nikt nie został zabity w walce
    }


    @Override
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

