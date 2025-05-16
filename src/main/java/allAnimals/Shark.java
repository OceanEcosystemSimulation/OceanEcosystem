package allAnimals;

import body.*;
import extendedMechanics.Reproduction;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import map.*;
import ocean.Main;
import ocean.World;
import ocean.WorldSearch;

import java.util.List;
import java.util.Objects;


public class Shark extends Carnivorous {
    public Shark(Coord position) {
        super(position, generateGenes());
        setName("Shark");
        settings();

    }

    //konstruktor dziecka
    public Shark(Coord position, Animal parent1, Animal parent2) {
        super(position, parent1, parent2);
        setName("Shark");
        settings();
    }


    /* -------------------------------GENES------------------------------- */

    //do tworzenia genów w nowych
    private static Genes generateGenes() {
        Genes genes = new Genes();
        genes.setStrength(5 + rand.nextInt(5));
        genes.setSpeed(5);
        genes.setMaxAge(100 + rand.nextInt(50));
        genes.setMaxLoneliness(40 + rand.nextInt(20));
        genes.setMaxEnergy(80);
        return genes;
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
        Reproduction.ReproductionProcess(this, mate);
        move(world); //wywołanie mechaniki ruchu
    }

    // tylko tata, bo kobieta rodzi
    @Override
    public Animal giveBirth(Coord position, Animal parent1, Animal parent2) {
        return new Shark(position, parent1, parent2);
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

    /* -------------------------------GRAPHICS------------------------------- */

    // Shark < 18

    private static final Image YoungShark = new Image(Objects.requireNonNull(Shark.class.getResource("/images/YoungShark.png")).toExternalForm());

    // Shark >= 18

    private static final int AGE_OLD = 18; // one turn = one month

    private static final Image OldShark = new Image(Objects.requireNonNull(Shark.class.getResource("/images/OldShark.png")).toExternalForm());
    private final ImageView imageView = new ImageView();
    public ImageView getImageView() {
        return imageView;
    } // getter

    /* -------------------------------GUI------------------------------- */

    private void settings() {
        imageView.setPreserveRatio(true);
        updateSharkGraphics();
    }

    private void updateSharkGraphics() {
        boolean isYoung = getAge() < AGE_OLD;
        imageView.setImage(isYoung ? YoungShark : OldShark);

        double scale = isYoung ? 0.8 : 1.0;
        imageView.setFitWidth(Main.getTileSize() * scale);
        imageView.setFitHeight(Main.getTileSize() * scale);
    }


}

