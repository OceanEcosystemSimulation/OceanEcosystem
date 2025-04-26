package allAnimals;

import ocean.*;

import java.util.List;

//znowu - IMove można przenieść jak potrzeba
public class Shark extends Carnivorous implements IMove, IEat {

    public Shark(Coord position) {
        super(position);
        setMaxAge(150 + rand.nextInt(30)); //do zmiany
        setMaxLoneliness(70); //do zmiany
    }

    @Override
    public void update(World world) {
        processLifeCycle();
        if (!isAlive()) {return;}

        move(world);


        //szukanie organizmu w pobliżu, który nie przekroczy limitu foodLevel
        List<Animal> nearby = world.getNearbyAnimals(getPosition(), 1); //pobiera zwierzęta w zasięgu ataku - zasięg do zmiany
        Animal bestTarget = null;
        int bestGain = Integer.MIN_VALUE; //najlepszy przyrost jedzenia, zaczynamy od minimalnego

        for (Animal a : nearby) {
            if (a != this && canAttack(a)) { //czy można zaatakować
                int potentialGain = calculateGain(a); //na razie stała bo idk co zakładamy
                if (getFoodLevel()+potentialGain <= 100 && potentialGain>bestGain) { //sprawdza czy nie przekracza i znajduje największy gain
                    bestTarget = a; //aktualizuje cel na najlepszy możliwy
                    bestGain = potentialGain;
                }
            }
        }

        //jeśli znaleziono cel spełniający warunki i atakuje (magia licząca czy wygra)
        if (bestTarget != null && attack(bestTarget)) {
            setFoodLevel(getFoodLevel() + bestGain); //aktualizacja foodLevel po sukcesie
        }
    }

    @Override
    public void move(World world){
        setPosition(getPosition().randomAdjacent(world.getWidth(), world.getHeight()));
    }


    @Override
    public boolean canAttack(Animal other) { //przykładowe że może atakować tylko Fish
        return other instanceof Fish; //albo dać instance od Herbivorous jak będziemy potrzebować
        // albo jest opcja jeszcze && other.genes.strenght < ... - jaki przykład używania po prostu
    }

    //przykładowe to wpisywania ile jakie jedzenie daje
    private int calculateGain(Animal a) {
        int baseGain = 0;
        if (a instanceof Fish) {
            baseGain = 30;
        }
        // itd
        return baseGain;
    }


    @Override
    public boolean canEat(Tile tile) { //also przykładowe
        return getFoodLevel() <= 30 &&
                (tile.foodType == FoodType.PLANKTON || tile.foodType == FoodType.ALGAE);
    }

    @Override
    public void eat(Tile tile) { //przykładowe jak pisać
        int gain = switch (tile.foodType) {
            case PLANKTON, ALGAE -> 5;
            default -> 0; //NONE
        };
        if (getFoodLevel()+gain <= 100){
            setFoodLevel(getFoodLevel() + gain);
            tile.clearFood();
        }
    }

}

