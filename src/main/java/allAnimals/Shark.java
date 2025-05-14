package allAnimals;

import body.*;
import map.*;
import ocean.World;
import ocean.WorldSearch;

import java.util.List;

import static body.AnimalLifeManager.canReproduce;
import static map.Coord.meetingAtMiddle;

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
        return switch (animal.getName()) {
            case "Fish" -> 30;
            //itd inne
            default -> 0;
        };
    }


    @Override
    public void eat(Tile tile) { //przykładowe jak pisać
        int gain = switch (tile.getFoodType()) {
            case PLANKTON, ALGAE -> 5;
            default -> 0; //NONE
        };
        if (getFoodLevel()+gain <= 100){ //tak na wszelki
            setFoodLevel(getFoodLevel() + gain);
            System.out.println(this.getName() + " id: " + this.getId() + " eats " + tile.getFoodType());
            tile.clearFood();
        }
    }


    //szuka partnera
    private void tryToMate(World world) {
        if (isAlive()) {
            Animal mate = WorldSearch.nearestMate(world, this.getPosition(), this.getGenes().getSpeed(), this); //znajduje mate
            if (mate!=null && mate.getGender()!=this.getGender()) { //przeciwna płeć
                Coord meetingPointA = meetingAtMiddle(world.getWidth(), world.getHeight(), this.getPosition(), mate.getPosition());
                Coord meetingPointB = meetingAtMiddle(world.getWidth(), world.getHeight(), this.getPosition(), mate.getPosition());
                this.setPosition(meetingPointA); //skok do A
                mate.setPosition(meetingPointB); //skok do B

                if (canReproduce(this) && canReproduce(mate)) {
                    System.out.println(this.getName() + " id: " + this.getId() + "  reproduce with  " + mate.getName() + " id: " + mate.getId());
                    //losowanie nowych współrzędnych w zasięgu jednej kratki od aktualnej pozycji rodzica
                    Coord childPosition = this.getPosition().randomAdjacent(world.getWidth(), world.getHeight(), 1, world);

                    Animal child = new Shark(childPosition, this, mate); //tworzenie nowego dzieciaka
                    world.addAnimal(child); //dodanie dzieciaka do świata
                    System.out.println(child.getName() + " id: " + child.getId() + "  was born");
                }
            }
        }
    }

}

