package allAnimals;

import body.*;
import extendedMechanics.Reproduction;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import map.*;
import movement.IEat;
import ocean.Main;
import ocean.World;

import java.util.List;
import java.util.Objects;


public class Shark extends Carnivorous implements IEat {
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
        genes.setStrength(40);
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


        tryToEat(world, this); //wywołanie mechaniki jedzenia
        tryToMate(world, this); //wywołanie mechaniki rozmnażania
        move(world); //wywołanie mechaniki ruchu
        tryToAttack(world, this); //wywołanie mechaniki ataku
    }

    // tylko tata, bo kobieta rodzi
    @Override
    public Animal giveBirth(Coord position, Animal parent1, Animal parent2) {
        return new Shark(position, parent1, parent2);
    }




    //stwierdziłam że dam tak bo bez sensu sie ma robić ciągle od nowa jak jest niezmienna
    private static final List<String> preyList = List.of("Nemo"); //lista kogo atakuje - do zmiany wartości (dodawane po przecinku jak coś)


    @Override
    public boolean canAttack(Animal other) {
        return other != null && other.getName() != null && preyList.contains(other.getName());
    }

    //przykładowe to wpisywania ile jakie jedzenie daje
    //można zrobić ifem jak wcześniej było jak wam nie pasuje takie
    @Override
    public int calculateGain(Animal animal) {
        return switch (animal.getName()) {
            case "Nemo" -> 30;
            //itd inne
            default -> 0;
        };
    }

    /* -------------------------------EATING------------------------------- */

    @Override
    public boolean canEat(Tile tile) { //also przykładowe
        return getFoodLevel() <= 30 &&
                (tile.getFoodType() == FoodType.PLANKTON || tile.getFoodType() == FoodType.ALGAE);
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

