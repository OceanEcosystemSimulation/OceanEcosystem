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


public class Octopus extends Carnivorous implements IEat {
    public Octopus(Coord position) {
        super(position, generateGenes());
        setName("Octopus");
        settings();

    }

    //konstruktor dziecka
    public Octopus(Coord position, Animal parent1, Animal parent2) {
        super(position, parent1, parent2);
        setName("Octopus");
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


    //ink
    private void releaseInk(World world) {
        int radius = 2;
        boolean released = false;

        //wypuszcza atrament jesli poczuje sie zgrozona
        for (Animal other : world.getNearbyAnimals(getPosition(), radius)) {
            if (!other.isAlive() || other == this) {continue;}

            if ("Dolphin".equals(other.getName())) {
                AnimalEffectsManager.slowTarget(this, other, 3);
                released = true;
            }
        }

        if (released) {
            InkCloud cloud = new InkCloud(getPosition());
            world.addObject(cloud);
        }
    }

    /* -------------------------------LIFE------------------------------- */

    @Override
    public void update(World world) {

        processLifeCycle(world); //duperele o życiu
        updateOctopusGraphics();

        if (!isAlive()) return;

        Reproduction.pregnancyTick(world, this);

        tryToEat(world, this); //wywołanie mechaniki jedzenia
        tryToMate(world); //wywołanie mechaniki rozmnażania
        move(world); //wywołanie mechaniki ruchu
        tryToAttack(world, this); //wywołanie mechaniki ataku
        releaseInk(world);

    }


    @Override
    public Animal giveBirth(Coord position, Animal parent1, Animal parent2) {
        return new Octopus(position, parent1, parent2);
    }




    private static final List<String> preyList = List.of("Nemo", "Shark"); //lista kogo atakuje


    @Override
    public boolean canAttack(Animal other) {
        return other != null && other.getName() != null && preyList.contains(other.getName());
    }


    @Override
    public int calculateGain(Animal animal) {
        return switch (animal.getName()) {
            case "Nemo" -> 30;
            case "Shark" -> 60;
            default -> 0;
        };
    }

    /* -------------------------------EATING------------------------------- */

    @Override
    public boolean canEat(Tile tile) {
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


    /* -------------------------------GRAPHICS------------------------------- */

    private static Image babyOctopus;
    private static Image Octopus;
    private static final int AGE_OLD = 18; // one turn = one month

    private final ImageView imageView = new ImageView();
    public ImageView getImageView() { return imageView; } // getter

    /* -------------------------------GUI------------------------------- */

    private static void loadImagesIfNeeded() {
        if (babyOctopus == null || Octopus == null) {
            if (!GraphicsEnvironment.isHeadless()) { //sprawdza czy jest srodowisko graficzne (testy go nie mają)
                babyOctopus = new Image(Objects.requireNonNull(Octopus.class.getResource("/images/babyOctopus.png")).toExternalForm());
                Octopus = new Image(Objects.requireNonNull(Octopus.class.getResource("/images/Octopus.png")).toExternalForm());
            }
        }
    }

    private void settings() {
        imageView.setPreserveRatio(true);
        updateOctopusGraphics();
    }

    private void updateOctopusGraphics() {
        loadImagesIfNeeded();

        boolean isYoung = getAge() < AGE_OLD;
        imageView.setImage(isYoung ? babyOctopus : Octopus);

        double scale = isYoung ? 0.8 : 1.0;
        imageView.setFitWidth(Main.getTileSize() * scale);
        imageView.setFitHeight(Main.getTileSize() * scale);
    }


}

