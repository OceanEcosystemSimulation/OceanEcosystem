package allAnimals;

import body.*;
import map.*;
import ocean.Main;
import ocean.World;
import extendedMechanics.Reproduction;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import ocean.WorldSearch;

import java.awt.*;
import java.util.List;
import java.util.Objects;

import static body.AnimalCombatUtils.randomMove;

//inheritance from Carnivorous
/**
 * Represents an Orca, inheriting from Carnivorous.
 */
public class Orca extends Carnivorous {
    //encapsulation
    private int boostTurns = 0;//przyspieszenie ile zostalo
    private int boostWaiting = 0; //odpoczynek po booscie


    public Orca(Coord position) {
        super(position, generateGenes());
        setName("Orca");
        settings();
    }

    public Orca(Coord position, Animal parent1, Animal parent2) {
        super(position, parent1, parent2);
        setName("Orca");
        settings();
    }

    /**
     * Generates default genetic attributes.
     * @return The genes object containing attributes.
     */
    private static Genes generateGenes() {
        Genes genes = new Genes();
        genes.setStrength(70);
        genes.setSpeed(5);
        genes.setMaxAge(90);
        genes.setMaxLoneliness(50);
        genes.setMaxEnergy(100);
        return genes;
    }


    @Override
    public void update(World world) {  //polymorphism
        processLifeCycle(world);
        updateOrcaGraphics();
        if (!isAlive()) { return; }

        Reproduction.pregnancyTick(world, this);

        tryToMate(world);
        move(world);
        tryToAttack(world, this);
    }


    @Override
    public Animal giveBirth(Coord position, Animal parent1, Animal parent2) {  //polymorphism
        return new Orca(position, parent1, parent2);
    }



    private static final List<String> preyList = List.of("Nemo", "Shark");


    @Override
    public boolean canAttack(Animal other) {
        return other != null && preyList.contains(other.getName());
    }


    @Override
    public int calculateGain(Animal animal) {  //polymorphism
        return switch (animal.getName()) {
            case "Nemo" -> 30;
            case "Shark" -> 40;
            default -> 0;
        };
    }


    /**
     * Handles the movement of the Orca, considering speed, prey, and temporary boosts.
     * If prey is nearby, the animal accelerates for a short duration.
     * After using a speed boost, the animal requires a cooldown before boosting again.
     * If food level is below 70, it prioritizes moving toward prey, otherwise it moves randomly.
     * @param world The world in which the animal exists.
     */
    @Override
    public void move(World world) {  //polymorphism
        int speed = getGenes().getSpeed();

        boolean preyNearby = false;
        //jak odpoczela i nie ma przyspieszenia to szuka nowej ofiary
        if (boostTurns == 0 && boostWaiting == 0) {
            preyNearby = WorldSearch.nearestPrey(world, getPosition(), 3, this) != null;
        }

        //boost i odpoczynek
        if (boostTurns > 0) {
            boostTurns--;
            speed += 3;
            if (boostTurns == 0) {
                boostWaiting = 5;
            }
        } else if (boostWaiting > 0) {
            boostWaiting--;
        } else if (preyNearby) {
            boostTurns = 2;
            speed += 3;
        }

        if (getFoodLevel() < 70) {
            Coord preyPos = WorldSearch.nearestPrey(world, getPosition(), speed, this);
            if (preyPos != null) {
                setPosition(preyPos);
                return;
            }
        }

        randomMove(world, this);
    }



    /* -------------------------------GRAPHICS------------------------------- */

    private static Image BabyOrca;
    private static Image AdultOrca;

    private final ImageView imageView = new ImageView();
    @Override
    public ImageView getImageView() {
        return imageView;
    }

    /**
     * Loads animal images if not already loaded or if there is graphic environment.
     */
    private static void loadImagesIfNeeded() {
        if (BabyOrca == null || AdultOrca == null) {
            if (!GraphicsEnvironment.isHeadless()) { //sprawdza czy jest srodowisko graficzne (testy go nie mają)
                BabyOrca = new Image(Objects.requireNonNull(Orca.class.getResource("/images/babyOrca.png")).toExternalForm());
                AdultOrca = new Image(Objects.requireNonNull(Orca.class.getResource("/images/adultOrca.png")).toExternalForm());
            }
        }
    }

    /**
     * Initializes graphical settings.
     */
    private void settings() {
        imageView.setPreserveRatio(true);
        updateOrcaGraphics();
    }

    /**
     * Updates the graphical representation of the animal based on its age.
     */
    private void updateOrcaGraphics() {
        loadImagesIfNeeded();

        boolean isYoung = getAge() < getMajority();
        imageView.setImage(isYoung ? BabyOrca : AdultOrca);

        double scale = isYoung ? 0.7 : 1.0;
        imageView.setFitWidth(Main.getTileSize() * scale);
        imageView.setFitHeight(Main.getTileSize() * scale);
    }
}