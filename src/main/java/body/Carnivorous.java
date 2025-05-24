package body;

import map.*;
import movement.*;
import ocean.World;
import ocean.WorldSearch;

import static body.AnimalCombatUtils.randomMove;


//abstract bo nie ma update
public abstract class Carnivorous extends Animal implements IFight, IMove, IMate {
    public Carnivorous(Coord position, Genes genes) {
        super(position, genes);
    }

    public Carnivorous(Coord position, Animal parent1, Animal parent2) {
        super(position, parent1, parent2);
    }


    //mechanika ruchu
    @Override
    public void move(World world) {
        if (getFoodLevel() < 70) {
            Coord preyPos = WorldSearch.nearestPrey(world, getPosition(), getGenes().getSpeed(), this); //szuka ofiary najblizszej
            if (preyPos != null) {
                setPosition(preyPos); //skok do ofiary
                return;
            } else if (getFoodLevel() < 30) { //jeżeli nie ma ofiary i jest super głodny
                Tile foodTile = WorldSearch.nearestFood(world, getPosition(), getGenes().getSpeed()); //szuka najbliższe jedzenie
                if (foodTile != null) {
                    Coord foodPos = new Coord(foodTile.getX(), foodTile.getY());
                    setPosition(foodPos); //skok do jedzenia
                    return;
                }
            }
        }
        randomMove(world, this); //randomowo gdy nie głodny lub brak jedzenia i ofiary
    }


    public boolean attack(Animal prey, World world) {
        double attackerSpeed = AnimalCombatUtils.getEffectiveSpeed(this); //predator speed
        double preySpeed = AnimalCombatUtils.getEffectiveSpeed(prey); //prey speed

        //próba ucieczki ofiary
        if (attackerSpeed < preySpeed) {
            AnimalCombatUtils.escape(world, prey);
            System.out.println(prey.getName() + " id: " + prey.getId() + "  escape from  " + this.getName() + " id: " + this.getId());
            return false; //ucieczka udana - brak walki
        }

        //walka
        double attackerPower = AnimalCombatUtils.getCombatPower(this);
        double preyPower = AnimalCombatUtils.getCombatPower(prey);

        int rounds = 5; //maksymalnie X wymian ciosów
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
            System.out.println(prey.getName() + " id: " + prey.getId() + "  escape from  " + this.getName() + " id: " + this.getId() + "  after " + rounds + " turns");
        } else {
            AnimalCombatUtils.escape(world, this);
            System.out.println(this.getName() + " id: " + this.getId() + "  escape from  " + prey.getName() + " id: " + prey.getId() + "  after " + rounds + " turns");
        }
        return false; //nikt nie został zabity w walce
    }
}

