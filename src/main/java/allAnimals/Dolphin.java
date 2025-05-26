package allAnimals;

import body.*;
import extendedMechanics.Reproduction;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import map.*;
import movement.IEat;
import ocean.Main;
import ocean.World;

import java.awt.*;
import java.util.List;
import java.util.Objects;


public class Dolphin extends Carnivorous implements IEat {
    public Dolphin(Coord position) {
        super(position, generateGenes());
        setName("Dolphin");
        settings();

    }

    //konstruktor dziecka
    public Dolphin(Coord position, Animal parent1, Animal parent2) {
        super(position, parent1, parent2);
        setName("Dolphin");
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
        updateDolphinGraphics();
        AnimalEffectsManager.updateInkEffect(this);

        if (!isAlive()) return;

        Reproduction.pregnancyTick(world, this);


        tryToEat(world, this); //wywołanie mechaniki jedzenia
        tryToMate(world); //wywołanie mechaniki rozmnażania
        move(world); //wywołanie mechaniki ruchu
        tryToAttack(world, this); //wywołanie mechaniki ataku
    }


    @Override
    public Animal giveBirth(Coord position, Animal parent1, Animal parent2) {
        return new Dolphin(position, parent1, parent2);
    }


    private static final List<String> preyList = List.of("Nemo", "Shark", "Orca"); //lista kogo atakuje


    @Override
    public boolean canAttack(Animal other) {
        return other != null && other.getName() != null && preyList.contains(other.getName());
    }


    @Override
    public int calculateGain(Animal animal) {
        return switch (animal.getName()) {
            case "Nemo" -> 30;
            case "Shark" -> 55;
            case "Orca" -> 90; // bo raczej nie wygra
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
    public void eat(Tile tile, World world) {
        int gain = switch (tile.getFoodType()) {
            case PLANKTON, ALGAE -> 5;
            default -> 0; //NONE
        };
        if (getFoodLevel()+gain <= 100){ //tak na wszelki
            setFoodLevel(getFoodLevel() + gain);
            System.out.println(this.getName() + " id: " + this.getId() + " eats " + tile.getFoodType());
            tile.clearFood(world);
        }
    }

    /* -------------------------------MOVE------------------------------- */

    @Override
    public void move(World world) {
        if (getSlowCounter() > 0) {
            System.out.println("Dolphin id: " + getId() + " is slowed by ink");
            return;
        }

        //jak nie jest spowolniony to ruch bez zmian
        super.move(world);
    }

    /* -------------------------------GRAPHICS------------------------------- */

    private static Image babyDolphin;
    private static Image dolphin;
    private static final int AGE_OLD = 18; // one turn = one month

    private final ImageView imageView = new ImageView();
    public ImageView getImageView() { return imageView; } // getter

    /* -------------------------------GUI------------------------------- */

    private static void loadImagesIfNeeded() {
        if (babyDolphin == null || dolphin == null) {
            if (!GraphicsEnvironment.isHeadless()) { //sprawdza czy jest srodowisko graficzne (testy go nie mają)
                babyDolphin = new Image(Objects.requireNonNull(Shark.class.getResource("/images/babyDolphin.png")).toExternalForm());
                dolphin = new Image(Objects.requireNonNull(Shark.class.getResource("/images/dolphin.png")).toExternalForm());
            }
        }
    }

    private void settings() {
        imageView.setPreserveRatio(true);
        updateDolphinGraphics();
    }

    private void updateDolphinGraphics() {
        loadImagesIfNeeded();

        boolean isYoung = getAge() < AGE_OLD;
        imageView.setImage(isYoung ? babyDolphin : dolphin);

        double scale = isYoung ? 0.8 : 1.0;
        imageView.setFitWidth(Main.getTileSize() * scale);
        imageView.setFitHeight(Main.getTileSize() * scale);
    }
}

