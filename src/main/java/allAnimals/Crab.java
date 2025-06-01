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

public class Crab extends Omnivorous {

    public Crab(Coord position) {
        super(position, generateGenes());
        setName("Crab");
        settings();
    }

    //konstruktor dziecka
    public Crab(Coord position, Animal parent1, Animal parent2) {
        super(position, parent1, parent2);
        setName("Crab");
        settings();
    }

    private static Genes generateGenes() {
        Genes genes = new Genes();
        genes.setStrength(Genes.mutate(10));
        genes.setSpeed(3);
        genes.setMaxAge(Genes.mutate(40));
        genes.setMaxLoneliness(Genes.mutate(40));
        genes.setMaxEnergy(100);
        return genes;
    }

    @Override
    public Animal giveBirth(Coord position, Animal parent1, Animal parent2) {
        return new Crab(position, parent1, parent2);
    }

    @Override
    public void update(World world) {
        processLifeCycle(world);
        updateCrabGraphics();
        if (!isAlive()) return;

        Reproduction.pregnancyTick(world, this);

        tryToEat(world, this);
        tryToMate(world);
        move(world);
        tryToAttack(world, this);
    }

    //jedzenie

    private static final List<String> animalPrey = List.of("Nemo");

    //gain ile dostaje za okreslone roslinki
    @Override
    public void eat(Tile tile, World world) {
        int gain = switch (tile.getFoodType()) {
            case ALGAE -> 10;
            case PLANKTON -> 8;
            default -> 0;
        };

        if (getFoodLevel() + gain <= 100) {
            setFoodLevel(getFoodLevel() + gain);
            SimulationStatsManager.writeToFile(this.getName() + "," + this.getId() + ",eats,,," + tile.foodType + "\n");
            tile.clearFood(world);
        }
    }

    //TODO: check czy nie trzeba tego przeniesc bo wszedzie takie samo

    //atakuje jak nie jest to zweirze tego samego gatunku i jest to zwierze ktora jest "ofiara" tego gatunku
    @Override
    public boolean canAttack(Animal other) {
        return other != null && animalPrey.contains(other.getName());
    }

    @Override
    public int calculateGain(Animal animal) {
        return switch (animal.getName()) {
            case "Nemo" -> 20;
            default -> 5;
        };
    }

    //grafika

    private static Image babyImage;
    private static Image adultImage;
    private static final int AGE_OLD = 15;

    private final ImageView imageView = new ImageView();
    public ImageView getImageView() { return imageView; }

    private static void loadImagesIfNeeded() {
        if (babyImage == null || adultImage == null) {
            if (!GraphicsEnvironment.isHeadless()) {
                babyImage = new Image(Objects.requireNonNull(Crab.class.getResource("/images/babyCrab.png")).toExternalForm());
                adultImage = new Image(Objects.requireNonNull(Crab.class.getResource("/images/adultCrab.png")).toExternalForm());
            }
        }
    }

    private void settings() {
        imageView.setPreserveRatio(true);
        updateCrabGraphics();
    }

    private void updateCrabGraphics() {
        loadImagesIfNeeded();
        boolean isYoung = getAge() < AGE_OLD;
        imageView.setImage(isYoung ? babyImage : adultImage);

        double scale = isYoung ? 0.8 : 1.0;
        imageView.setFitWidth(Main.getTileSize() * scale);
        imageView.setFitHeight(Main.getTileSize() * scale);
    }
}
