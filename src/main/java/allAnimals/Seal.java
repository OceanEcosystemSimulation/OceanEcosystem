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

//inheritance from Carnivorous
/**
 * Represents a Seal, inheriting from Carnivorous.
 */
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

    /**
     * Generates default genetic attributes.
     * @return The genes object containing attributes.
     */
    private static Genes generateGenes() {
        Genes genes = new Genes();
        genes.setStrength(40);
        genes.setSpeed(3);
        genes.setMaxAge(55);
        genes.setMaxLoneliness(45);
        genes.setMaxEnergy(100);
        return genes;
    }


    @Override
    public void update(World world) {  //polymorphism
        processLifeCycle(world);
        scareNearbySharks(world);
        updateSealGraphics();
        if (!isAlive()) { return; }

        Reproduction.pregnancyTick(world, this);

        tryToMate(world);
        tryToAttack(world, this);
        move(world);
    }


    @Override
    public Animal giveBirth(Coord position, Animal parent1, Animal parent2) {  //polymorphism
        return new Seal(position, parent1, parent2);
    }


    //lista ofiar
    private static final List<String> preyList = List.of("Nemo", "Crab");


    @Override
    public boolean canAttack(Animal other) {
        return other != null && preyList.contains(other.getName());
    }


    @Override
    public int calculateGain(Animal animal) {  //polymorphism
        return switch (animal.getName()) {
            case "Nemo" -> 15;
            case "Crab" -> 20;
            default -> 0;
        };
    }


    /**
     * Causes nearby sharks within a radius of 3 tiles to flee.
     * Logs the event and triggers an escape mechanism for affected sharks.
     * @param world The world in which the animal exists.
     */
    private void scareNearbySharks(World world) {
        List<Animal> nearbyAnimals = world.getNearbyAnimals(getPosition(), 3); //bierze zwierzeta w zasiegu 3

        for (Animal animal : nearbyAnimals) { //iteruje po znalezionych
            if (animal.getName() != null && animal.getName().equals("Shark")) {
                SimulationStatsManager.writeToFile("Seal," + getId() + ",scared away,Shark," + animal.getId() + "\n");
                AnimalCombatUtils.escape(world, animal); //rekin ucieka
            }
        }
    }



    /* -------------------------------GRAPHICS------------------------------- */

    private static Image babySeal;
    private static Image adultSeal;

    private final ImageView imageView = new ImageView();
    @Override
    public ImageView getImageView() {
        return imageView;
    }

    /**
     * Loads animal images if not already loaded or if there is graphic environment.
     */
    private static void loadImagesIfNeeded() {
        if (babySeal == null || adultSeal == null) {
            if (!GraphicsEnvironment.isHeadless()) {
                babySeal = new Image(Objects.requireNonNull(Seal.class.getResource("/images/babySeal.png")).toExternalForm());
                adultSeal = new Image(Objects.requireNonNull(Seal.class.getResource("/images/adultSeal.png")).toExternalForm());
            }
        }
    }

    /**
     * Initializes graphical settings.
     */
    private void settings() {
        imageView.setPreserveRatio(true);
        updateSealGraphics();
    }

    /**
     * Updates the graphical representation of the animal based on its age.
     */
    private void updateSealGraphics() {
        loadImagesIfNeeded();

        boolean isYoung = getAge() < getMajority();
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
