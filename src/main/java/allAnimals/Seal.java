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


public class Seal extends Carnivorous {

    public Seal(Coord position) {
        super(position, generateGenes());
        setName("Seal");
        settings();
    }

    //konstruktor dziecka
    public Seal(Coord position, Animal parent1, Animal parent2) {
        super(position, parent1, parent2);
        setName("Seal");
        settings();
    }

    private static Genes generateGenes() {
        Genes genes = new Genes();
        genes.setStrength(Genes.mutate(40));
        genes.setSpeed(3);
        genes.setMaxAge(Genes.mutate(55));
        genes.setMaxLoneliness(Genes.mutate(45));
        genes.setMaxEnergy(100);
        return genes;
    }

    @Override
    public Animal giveBirth(Coord position, Animal parent1, Animal parent2) {
        return new Seal(position, parent1, parent2);
    }

    //lista ofiar
    private static final List<String> preyList = List.of("Nemo", "Crab");

    //atakuje jak nie jest to zweirze tego samego gatunku i jest to zwierze ktora jest "ofiara" tego gatunku
    @Override
    public boolean canAttack(Animal other) {
        return other != null && preyList.contains(other.getName());
    }

    @Override
    public int calculateGain(Animal animal) {
        return switch (animal.getName()) {
            case "Nemo" -> 15;
            case "Crab" -> 20;
            default -> 0;
        };
    }

    //straszy rekiny w zasięgu 3 kratek
    private void scareNearbySharks(World world) {
        List<Animal> nearbyAnimals = world.getNearbyAnimals(getPosition(), 3); //bierze zwierzeta w zasiegu 3

        for (Animal animal : nearbyAnimals) { //iteruje po znalezionych
            if (animal.getName() != null && animal.getName().equals("Shark")) {
                SimulationStatsManager.writeToFile("Seal," + getId() + ",scared away,Shark," + animal.getId() + "\n");
                AnimalCombatUtils.escape(world, animal); //rekin ucieka
            }
        }
    }



    @Override
    public void update(World world) {
        processLifeCycle(world);
        scareNearbySharks(world);
        updateSealGraphics();
        if (!isAlive()) return;

        Reproduction.pregnancyTick(world, this);

        tryToMate(world);
        tryToAttack(world, this);
        move(world);
    }

    /* ---------- GRAFIKA ---------- */

    private static final int AGE_OLD = 18;
    private static Image babySeal;
    private static Image adultSeal;

    private final ImageView imageView = new ImageView();
    public ImageView getImageView() {
        return imageView;
    }

    private static void loadImagesIfNeeded() {
        if (babySeal == null || adultSeal == null) {
            if (!GraphicsEnvironment.isHeadless()) {
                babySeal = new Image(Objects.requireNonNull(Seal.class.getResource("/images/babySeal.png")).toExternalForm());
                adultSeal = new Image(Objects.requireNonNull(Seal.class.getResource("/images/adultSeal.png")).toExternalForm());
            }
        }
    }

    private void settings() {
        imageView.setPreserveRatio(true);
        updateSealGraphics();
    }

    private void updateSealGraphics() {
        loadImagesIfNeeded();

        boolean isYoung = getAge() < AGE_OLD;
        if (isYoung) {
            imageView.setImage(babySeal);
        } else {
            imageView.setImage(adultSeal);
        }

        double scale;
        if (isYoung) {
            scale = 0.75;
        } else {
            scale = 1.0;
        }
        imageView.setFitWidth(Main.getTileSize() * scale);
        imageView.setFitHeight(Main.getTileSize() * scale);
    }
}
