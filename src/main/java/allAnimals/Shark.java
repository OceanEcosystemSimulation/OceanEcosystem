package allAnimals;

import ocean.*;

import java.util.List;

public class Shark extends Carnivorous {
    public Shark(Coord position) {
        super(position, generateGenes(), 150 + rand.nextInt(30), 70);
        //wartości maxAge i maxLoneliness do zmiany
        setName("Shark");
    }

    public Shark(Coord position, Animal parent1, Animal parent2) {
        super(position, parent1, parent2);  //konstruktor dziecka
        setName("Shark");
    }



    //do tworzenia genów w nowych - zakresy w losowych wartościah do zmiany
    private static Genes generateGenes() {
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

        tryToAttack(world);
        tryToEat(world);
        tryToMate(world);
    }



    //sprawdzenie czy na obecnej pozycji znajduje się ofiara
    private void tryToAttack(World world) {
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
    }


    //stwierdziłam że dam tak bo bez sensu sie ma robić ciągle od nowa jak jest niezmienna
    private static final List<String> preyList = List.of("Fish"); //lista kogo atakuje - do zmiany wartości (dodawane po przecinku jak coś)

    @Override
    public boolean canAttack(Animal other) {
        return other != null && preyList.contains(other.getName());  //czy imie gatunku znajduje się na liscie
    }


    //przykładowe to wpisywania ile jakie jedzenie daje
    //można zrobić ifem jak wcześniej było jak wam nie pasuje takie
    private int calculateGain(Animal animal) {
        int baseGain = switch (animal.getName()) {
            case "Fish" -> 30;
            //itd inne
            default -> 0;
        };
        return baseGain;
    }


    @Override
    public void eat(Tile tile) { //przykładowe jak pisać
        int gain = switch (tile.foodType) {
            case PLANKTON, ALGAE -> 5;
            default -> 0; //NONE
        };
        if (getFoodLevel()+gain <= 100){ //tak na wszelki
            setFoodLevel(getFoodLevel() + gain);
            System.out.println(this.getName() + " id: " + this.getId() + " eats " + tile.foodType);
            tile.clearFood();
        }
    }

}

