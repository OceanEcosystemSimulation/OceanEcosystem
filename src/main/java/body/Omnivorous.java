package body;

import map.*;
import movement.*;
import ocean.World;
import ocean.WorldSearch;

import static body.AnimalCombatUtils.randomMove;


public abstract class Omnivorous extends Animal implements IEat, IFight, IMate {
    public Omnivorous(Coord position, Genes genes) {
        super(position, genes);
    }

    public Omnivorous(Coord position, Animal parent1, Animal parent2) {
        super(position, parent1, parent2);
    }

    @Override
    public boolean canEat(Tile tile) {
        //moze jesc algi i plankton
        return tile.getFoodType() == FoodType.PLANKTON || tile.getFoodType() == FoodType.ALGAE;
    }


    public void move(World world) {
        if (getFoodLevel() < 70) {
            //szuka ofiary
            Coord preyPos = WorldSearch.nearestPrey(world, getPosition(), getGenes().getSpeed(), this);
            if (preyPos != null) {
                setPosition(preyPos);
                return;
            }

            //jak nie ma w poblizu ofiary to szuka roslinek
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

    //atak
    public boolean attack(Animal prey, World world) {
        double attackerSpeed = AnimalCombatUtils.getEffectiveSpeed(this);
        double preySpeed = AnimalCombatUtils.getEffectiveSpeed(prey);

        if (attackerSpeed < preySpeed) {
            AnimalCombatUtils.escape(world, prey);
            System.out.println(prey.getName() + " id: " + prey.getId() + " escaped from " + this.getName() + " id: " + this.getId());
            return false;
        }

        double attackerPower = AnimalCombatUtils.getCombatPower(this);
        double preyPower = AnimalCombatUtils.getCombatPower(prey);

        int rounds = 5;
        for (int i = 0; i < rounds; i++) {
            AnimalCombatUtils.takeDamage(prey, attackerPower);
            if (!prey.isAlive()) {
                System.out.println(this.getName() + " id: " + this.getId() + " killed " + prey.getName() + " id: " + prey.getId());
                return true;
            }

            AnimalCombatUtils.takeDamage(this, preyPower);
            if (!this.isAlive()) {
                System.out.println(prey.getName() + " id: " + prey.getId() + " killed " + this.getName() + " id: " + this.getId());
                return false;
            }
        }

        if (attackerPower > preyPower) {
            AnimalCombatUtils.escape(world, prey);
            System.out.println(prey.getName() + " id: " + prey.getId() + " escaped from " + this.getName() + " id: " + this.getId());
        } else {
            AnimalCombatUtils.escape(world, this);
            System.out.println(this.getName() + " id: " + this.getId() + " escaped from " + prey.getName() + " id: " + prey.getId());
        }

        return false;
    }

    /*
    @Override
    public void eat(Tile tile, World world) { //whatever - do zmiany i tak
        int gain = switch (tile.getFoodType()) {
            case PLANKTON -> 10;
            case ALGAE -> 15;
            default -> 0;
        };
        if (getAge()+gain <= 100){
            setFoodLevel(getFoodLevel() + gain);
            tile.clearFood(world);
        }
    }
    */

}

