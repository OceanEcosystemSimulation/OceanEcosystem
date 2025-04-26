package allAnimals;

import ocean.*;

import java.util.List;

public class Shark extends Carnivorous implements IEat {

    public Shark(Coord position) {
        super(position);
        setGenes(generateGenes());
        setMaxAge(150 + rand.nextInt(30)); //do zmiany
        setMaxLoneliness(70); //do zmiany
    }


    //do tworzenia genów w nowych - zakresy w losowych wartościah do zmiany
    private Genes generateGenes() {
        Genes g = new Genes();
        g.setStrength(5 + rand.nextInt(5));
        g.setSpeed(10 + rand.nextInt(10));
        g.setFertility(20 + rand.nextInt(10));
        return g;
    }

    @Override
    public void update(World world) {
        processLifeCycle(world);
        if (!isAlive()) {return;}

        move(world);

        //sprawdzenie czy na obecnej pozycji znajduje się ofiara
        List<Animal> nearbyAnimals = world.getNearbyAnimals(getPosition(), 0); //pobiera zwierzęta na aktualnym polu
        for (Animal animal : nearbyAnimals) {
            if (animal!=this && canAttack(animal)) { //nie zjada sam siebie
                if (attack(animal, world)) { //udany atak
                    int gain = calculateGain(animal); //obliczanie gain z ataku na zwierzę
                    setFoodLevel(getFoodLevel() + gain); //aktualizacja poziomu jedzenia
                    return; //koniec akcji
                }
            }
        }

        //sprawdzenie czy na obecnym kafelku znajduje się jedzenie
        Tile currentTile = world.getTile(getPosition());
        if (currentTile != null && canEat(currentTile)) { //jeśli tile zawiera jedzenie i Shark może je jeść
            eat(currentTile); //je
        }
    }



    @Override
    public boolean canAttack(Animal other) { //przykładowe że może atakować tylko Fish
        return other instanceof Fish; //albo dać instance od Herbivorous jak będziemy potrzebować
        // albo jest opcja jeszcze && other.genes.strenght < ... - jaki przykład używania po prostu
    }

    //przykładowe to wpisywania ile jakie jedzenie daje
    private int calculateGain(Animal animal) {
        int baseGain = 0;
        if (animal instanceof Fish) {
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
        if (getFoodLevel()+gain <= 100){ //tak na wszelki
            setFoodLevel(getFoodLevel() + gain);
            tile.clearFood();
        }
    }

}

