package allAnimals;

import body.*;
import extendedMechanics.Reproduction;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import map.*;
import ocean.Main;
import ocean.SimulationStatsManager;
import ocean.World;

import java.awt.*;
import java.util.List;
import java.util.Objects;

public class Turtle extends Omnivorous {

    public Turtle(Coord position) {
        super(position, generateGenes());
        setName("Turtle");
        settings();
    }

    public Turtle(Coord position, Animal parent1, Animal parent2) {
        super(position, parent1, parent2);
        setName("Turtle");
        settings();
    }

    private static Genes generateGenes() {
        Genes genes = new Genes();
        genes.setStrength(Genes.mutate(30));
        genes.setSpeed(4);
        genes.setMaxAge(100);
        genes.setMaxLoneliness(Genes.mutate(60));
        genes.setMaxEnergy(100);
        return genes;
    }

    @Override
    public Animal giveBirth(Coord position, Animal parent1, Animal parent2) {
        return new Turtle(position, parent1, parent2);
    }

    @Override
    public void update(World world) {
        processLifeCycle(world);
        updateGraphics();
        if (!isAlive()) return;

        Reproduction.pregnancyTick(world, this);

        tryToEat(world, this);
        tryToMate(world);
        move(world);
        tryToAttack(world, this);
    }

    //jedzenie

    private static final List<String> animalPrey = List.of("Octopus", "Crab");

    @Override
    public boolean canEat(Tile tile) {
        return getFoodLevel() <= 70 &&
                (tile.getFoodType() == FoodType.ALGAE || tile.getFoodType() == FoodType.PLANKTON);
    }

    @Override
    public void eat(Tile tile, World world) {
        int gain = switch (tile.getFoodType()) {
            case ALGAE -> 15;
            case PLANKTON -> 10;
            default -> 0;
        };

        if (getFoodLevel() + gain <= 100) {
            setFoodLevel(getFoodLevel() + gain);
            SimulationStatsManager.writeToFile(this.getName() + " id: " + this.getId() + " eats " + tile.getFoodType());
            tile.clearFood(world);
        }
    }

    @Override
    public boolean canAttack(Animal other) {
        return other != null && animalPrey.contains(other.getName());
    }

    @Override
    public int calculateGain(Animal animal) {
        return switch (animal.getName()) {
            case "Octopus" -> 30;
            case "Crab" -> 20;
            default -> 5;
        };
    }

    //grafika

    private static Image babyImage;
    private static Image adultImage;
    private static final int AGE_OLD = 20;

    private final ImageView imageView = new ImageView();
    public ImageView getImageView() { return imageView; }

    private static void loadImagesIfNeeded() {
        if (babyImage == null || adultImage == null) {
            if (!GraphicsEnvironment.isHeadless()) {
                babyImage = new Image(Objects.requireNonNull(Turtle.class.getResource("/images/babyTurtle.png")).toExternalForm());
                adultImage = new Image(Objects.requireNonNull(Turtle.class.getResource("/images/adultTurtle.png")).toExternalForm());
            }
        }
    }

    private void settings() {
        imageView.setPreserveRatio(true);
        updateGraphics();
    }

    private void updateGraphics() {
        loadImagesIfNeeded();
        boolean isYoung = getAge() < AGE_OLD;
        if (isYoung) {
            imageView.setImage(babyImage);
        } else {
            imageView.setImage(adultImage);
        }
        double scale;
        if (isYoung) {
            scale = 0.8;
        } else {
            scale = 1.0;
        }

        imageView.setFitWidth(Main.getTileSize() * scale);
        imageView.setFitHeight(Main.getTileSize() * scale);
    }
}
