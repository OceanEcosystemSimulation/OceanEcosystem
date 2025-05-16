package allAnimals;

import body.*;
import extendedMechanics.Reproduction;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import map.*;
import ocean.World;
import ocean.WorldSearch;

import java.util.List;
import java.util.Objects;

import static body.AnimalLifeManager.canReproduce;
import static map.Coord.meetingAtMiddle;

public class Shark extends Carnivorous {

    /* -------------------------------GRAPHICS------------------------------- */

    // Shark < 18

    private static final Image YoungShark = new Image(Objects.requireNonNull(Shark.class.getResource("/images/YoungShark.png")).toExternalForm());

    // Shark >= 18

    private static final Image OldShark = new Image(Objects.requireNonNull(Shark.class.getResource("/images/OldShark.png")).toExternalForm());

    private static final int AGE_OLD = 18; // one turn = one month

    private final ImageView imageView = new ImageView();

    public ImageView getImageView() {
        return imageView;
    } // getter

    /* -------------------------------GUI------------------------------- */

    private void settings() {
        imageView.setPreserveRatio(true);
        updateSharkGraphics();
        setName("Shark");
    }

    private void updateSharkGraphics() {
        if (getAge() < AGE_OLD) {
            imageView.setImage(YoungShark);
            imageView.setFitWidth(World.TILE_SIZE * 0.8);
            imageView.setFitHeight(World.TILE_SIZE * 0.8);
        } else {
            imageView.setImage(OldShark);
            imageView.setFitWidth(World.TILE_SIZE);
            imageView.setFitHeight(World.TILE_SIZE);
        }
    }

    /* -------------------------------CONSTRUCTORS------------------------------- */

    public Shark(Coord position, Genes genes) {
        super(position, genes,
                150 + rand.nextInt(30),
                70 + rand.nextInt(20),
                90 + rand.nextInt(40));
        //wartości maxAge i maxLoneliness do zmiany
        settings();
        setName("Shark");

    }

    public Shark(Coord position, Animal parent1, Animal parent2) {
        super(position, parent1, parent2);  //konstruktor dziecka
        settings();
        setName("Shark");

    }

    /* -------------------------------GENES------------------------------- */

    //do tworzenia genów w nowych - zakresy w losowych wartościah do zmiany
    private static Genes generateGenes() {
        Genes g = new Genes();
        g.setStrength(5 + rand.nextInt(5));
        g.setSpeed(10 + rand.nextInt(10));
        g.setFertility(20 + rand.nextInt(10));
        g.setGender(rand.nextBoolean() ? Gender.FEMALE : Gender.MALE);
        return g;
    }

    /* -------------------------------LIFE------------------------------- */

    @Override
    public void update(World world) {
        processLifeCycle(world); //duperele o życiu
        updateSharkGraphics();
        if (!isAlive()) return;

        Reproduction.pregnancyTick(world, this);


        tryToEat(world); //wywołanie mechaniki jedzenia
        int radius = getGenes().getSpeed();
        Animal mate = WorldSearch.nearestMate(world, getPosition(), radius, this);
        Reproduction.ReproductionProcess(world, this, mate);
        move(world); //wywołanie mechaniki ruchu
    }

    // tylko tata, bo kobieta rodzi
    @Override
    public Animal giveBirth(Coord position, Genes genes) {
        return new Shark(position, genes);
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
    private static final List<String> preyList = List.of("Nemo"); //lista kogo atakuje - do zmiany wartości (dodawane po przecinku jak coś)


    /*
    @Override
    public boolean canAttack(Animal other) {
        return other != null && preyList.contains(other.getName());  //czy imie gatunku znajduje się na liscie
    }*/
    @Override
    public boolean canAttack(Animal other) {
        return other != null && other.getName() != null && preyList.contains(other.getName());
    }

    //przykładowe to wpisywania ile jakie jedzenie daje
    //można zrobić ifem jak wcześniej było jak wam nie pasuje takie
    private int calculateGain(Animal animal) {
        return switch (animal.getName()) {
            case "Nemo" -> 30;
            //itd inne
            default -> 0;
        };
    }

    /* -------------------------------EATING------------------------------- */

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
}

