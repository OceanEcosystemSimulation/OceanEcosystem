package allAnimals;
import body.*;
import map.*;
import ocean.World;
import ocean.WorldSearch;

import java.util.List;

import static body.AnimalLifeManager.canReproduce;
import static map.Coord.meetingAtMiddle;

/*public class Orca extends Carnivorous {

    public Orca(Coord position) {
        super(position, generateGenes(), 30, 60); //max loneliness randomowa wsm jak wymyslimy jaka to sie zmieni
        setName("Orca");
    }

    public Orca(Coord position, Animal parent1, Animal parent2) {
        super(position, parent1, parent2);
        setName("Orca");
    }

    private static Genes generateGenes() {
        Genes g = new Genes();
        g.setStrength(70 + rand.nextInt(11)); //narazie bez zwiekszania jak jest jedzenie
        g.setSpeed(70 + rand.nextInt(11)); //dalam tyle samo wiec mozna zmienic
        return g;
    }


    @Override
    public void update(World world) {
        processLifeCycle(world);
        if (!isAlive()) return;

        move(world);

        tryToAttack(world);
        tryToEat(world);
        tryToMate(world);
    }

    private void tryToAttack(World world) {
        List<Animal> nearbyAnimals = world.getNearbyAnimals(getPosition(), 0);

        for (Animal animal : nearbyAnimals) {
            if (animal != this && canAttack(animal)) {
                System.out.println("Orca id: " + getId() + " attacks " + animal.getName() + " id: " + animal.getId());
                if (attack(animal, world)) {
                    int gain = calculateGain(animal);
                    setFoodLevel(getFoodLevel() + gain);
                    return; //udala sie akcja
                }
            }

        }
    }

    //taka sama zasada jak w rekinie haha
    private static final List<String> preyList = List.of("Fish", "Shark");

    public boolean canAttack(Animal other) {
        return other != null && preyList.contains(other.getName());
    }

    private int calculateGain(Animal animal) {
        return switch (animal.getName()) {
            case "Fish" -> 30; //wartosci przykladowo
            case "Shark" -> 40;
            default -> 0;
        };
    }

    @Override
    public void eat(Tile tile) { //wiem ze to nic nie daje ale bez tego pokazywalo ze "public class orca ..." jest blednie

    }



    private void tryToMate(World world) {
        if (!isAlive()) return;

        Animal mate = WorldSearch.nearestMate(world, getPosition(), getSpeedForFood(world), this);
        if (mate != null && mate.getGender() != getGender()) { //sprawdza płeć
            Coord middleA = meetingAtMiddle(world.getWidth(), world.getHeight(), getPosition(), mate.getPosition());
            Coord middleB = meetingAtMiddle(world.getWidth(), world.getHeight(), getPosition(), mate.getPosition());
            this.setPosition(middleA);
            mate.setPosition(middleB);

            if (canReproduce(this) && canReproduce(mate)) {
                //szuka czy jest miejsce na dziecko, idk uznalam ze to moze pomoc z za duza iloscia zwierzac ale jak bez sensu to mozna usnac
                Coord childPos = getPosition().randomAdjacent(world.getWidth(), world.getHeight(), 1, world);
                if (childPos == null) {
                    System.out.println(getName() + " id: " + getId() + " and " +
                            mate.getName() + " id: " + mate.getId() +
                            " wanted to reproduce, but there was no space for a new baby.");
                    return;
                } else {
                    Animal child = new Orca(childPos, this, mate);
                    world.addAnimal(child);
                    System.out.println("Orca id: " + child.getId() + " was born");
                }
            }
        }
    }

    public int getSpeedForFood(World world) {
        if (world != null) {
            int x = getPosition().x;
            int y = getPosition().y;
            int range = 3;//wyszukuje posilku w odleglosci do 3 kratek

            for (int dx = -range; dx <= range; dx++) {//tam na minusie jest bo chce objac kratki przed tez
                for (int dy = -range; dy <= range; dy++) {
                    //przeszkuje kratki
                    int newX = x + dx;
                    int newY = y + dy;

                    if (newX >= 0 && newX < world.getWidth() && newY >= 0 && newY < world.getHeight()) { //sprawdza czy jest w granicach
                        Coord candidate = new Coord(newX, newY);
                        Tile tile = world.getTile(candidate); //bierze dane o polu

                        if (tile != null && tile.hasFood()) {
                            return getGenes().getSpeed() + 10; //boost jeśli znalazło jedzenie, przykladowo 10
                        }
                    }
                }
            }
        }

        //jesli world == null albo brak jedzenia to wtedy zwraca bazową prędkość
        return getGenes().getSpeed();
    }

}*/
