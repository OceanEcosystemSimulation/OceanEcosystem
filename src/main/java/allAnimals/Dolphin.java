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
 * Represents a Dolphin, inheriting from Carnivorous.
 */
public class Dolphin extends Carnivorous {
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


    /**
     * Generates default genetic attributes.
     * @return The genes object containing attributes.
     */
    private static Genes generateGenes() {
        Genes genes = new Genes();
        genes.setStrength(40);
        genes.setSpeed(5);
        genes.setMaxAge(60);
        genes.setMaxLoneliness(30);
        genes.setMaxEnergy(80);
        return genes;
    }


    /* -------------------------------LIFE------------------------------- */

    @Override
    public void update(World world) {  //polymorphism
        processLifeCycle(world);
        updateDolphinGraphics();
        AnimalEffectsManager.updateInkEffect(this);

        if (!isAlive()) { return; }

        Reproduction.pregnancyTick(world, this);

        tryToMate(world); //wywołanie mechaniki rozmnażania
        move(world); //wywołanie mechaniki ruchu
        tryToAttack(world, this); //wywołanie mechaniki ataku
    }


    @Override
    public Animal giveBirth(Coord position, Animal parent1, Animal parent2) {  //polymorphism
        return new Dolphin(position, parent1, parent2);
    }


    private static final List<String> preyList = List.of("Nemo", "Shark", "Orca"); //lista kogo atakuje


    @Override
    public boolean canAttack(Animal other) {
        return other != null && other.getName() != null && preyList.contains(other.getName());
    }


    @Override
    public int calculateGain(Animal animal) {  //polymorphism
        return switch (animal.getName()) {
            case "Nemo" -> 30;
            case "Shark" -> 55;
            case "Orca" -> 90;
            default -> 0;
        };
    }


    /* -------------------------------MOVE------------------------------- */

    private boolean stunned = false; //czy jest zatrzymany //encapsulation

    public boolean isStunned() {
        return stunned;
    }

    public void setStunned(boolean stunned) {
        this.stunned = stunned;
    }


    /**
     * Handles the movement of the Dolphin.
     * If stunned, movement is prevented and logged, otherwise normal movement occurs.
     * @param world The world in which the animal exists.
     */
    @Override
    public void move(World world) {  //polymorphism
        if (isStunned()) {
            SimulationStatsManager.writeToFile("Dolphin," + getId() + ",is stunned and cannot move\n");
            return;
        }
        super.move(world); //jak nie jest spowolniony to ruch bez zmian
    }


    /* -------------------------------GRAPHICS------------------------------- */

    private static Image babyDolphin;
    private static Image dolphin;

    private final ImageView imageView = new ImageView();
    @Override
    public ImageView getImageView() { return imageView; }


    /**
     * Loads Dolphin images if not already loaded or if there is graphic environment.
     */
    private static void loadImagesIfNeeded() {
        if (babyDolphin == null || dolphin == null) {
            if (!GraphicsEnvironment.isHeadless()) { //sprawdza czy jest srodowisko graficzne (testy go nie mają)
                babyDolphin = new Image(Objects.requireNonNull(Shark.class.getResource("/images/babyDolphin.png")).toExternalForm());
                dolphin = new Image(Objects.requireNonNull(Shark.class.getResource("/images/dolphin.png")).toExternalForm());
            }
        }
    }

    /**
     * Initializes graphical settings for the Dolphin.
     */
    private void settings() {
        imageView.setPreserveRatio(true);
        updateDolphinGraphics();
    }


    /**
     * Updates the graphical representation of the Crab based on its age.
     */
    private void updateDolphinGraphics() {
        loadImagesIfNeeded();

        boolean isYoung = getAge() < getMajority();
        imageView.setImage(isYoung ? babyDolphin : dolphin);

        double scale = isYoung ? 0.8 : 1.0;
        imageView.setFitWidth(Main.getTileSize() * scale);
        imageView.setFitHeight(Main.getTileSize() * scale);
    }
}

