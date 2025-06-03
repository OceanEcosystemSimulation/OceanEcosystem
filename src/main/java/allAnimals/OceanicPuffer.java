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

//inheritance from Omnivorous
/**
 * Represents a Oceanic Puffer, inheriting from Omnivorous.
 */
public class OceanicPuffer extends Omnivorous {
    public OceanicPuffer(Coord position) {
        super(position, generateGenes());
        setName("OceanicPuffer");
        settings();
    }

    //konstruktor dziecka
    public OceanicPuffer(Coord position, Animal parent1, Animal parent2) {
        super(position, parent1, parent2);
        setName("OceanicPuffer");
        settings();
    }


    /**
     * Generates default genetic attributes.
     * @return The genes object containing attributes.
     */
    private static Genes generateGenes() {
        Genes genes = new Genes();
        genes.setStrength(20);
        genes.setSpeed(2);
        genes.setMaxAge(30);
        genes.setMaxLoneliness(50);
        genes.setMaxEnergy(100);
        return genes;
    }


    @Override
    public Animal giveBirth(Coord position, Animal parent1, Animal parent2) {  //polymorphism
        return new OceanicPuffer(position, parent1, parent2);
    }

    @Override
    public void update(World world) {  //polymorphism
        processLifeCycle(world);
        updatePufferGraphics();
        if (!isAlive()) { return; }

        Reproduction.pregnancyTick(world, this);

        tryToEat(world, this);
        tryToMate(world);
        move(world);
        tryToAttack(world, this);
    }


    private static final List<String> preyList = List.of("Crab");


    //czy może atakować
    @Override
    public boolean canAttack(Animal other) {
        return other != null && other.getName() != null && preyList.contains(other.getName());
    }


    /**
     * Executes combat between this animal and a target.
     * Determines the winner based on combat power and applies effects accordingly.
     * If the attacker has higher power, the target dies, otherwise the attacker dies and poisons the opponent.
     * @param target The animal being attacked.
     * @param world The simulation world in which it happens.
     * @return True if the target is killed, otherwise false.
     */
    @Override
    public boolean attack(Animal target, World world) {   //polymorphism
        double attackerPower = AnimalCombatUtils.getCombatPower(this);
        double targetPower = AnimalCombatUtils.getCombatPower(target);

        if (attackerPower > targetPower) { //wygrywa pufferfish
            target.die(world);
            SimulationStatsManager.writeToFile(getName() + "," + getId() + ",killed," + target.getName() + "," + target.getId() + "\n");
            return true;
        } else {
            this.die(world); //Pufferfish ginie
            SimulationStatsManager.writeToFile(target.getName() + "," + target.getId() + ",killed," + getName() + "," + getId() + "\n");
            AnimalEffectsManager.poisonTarget(target); //zatruwa przeciwnika
            return false;
        }
    }

    //okreslenie ile punktow jedzenia dostanie za zjedzenie przeciwnika
    @Override
    public int calculateGain(Animal animal) {  //polymorphism
        return switch (animal.getName()) {
            case "Crab" -> 25;
            default -> 0;
        };
    }


    @Override
    public void eat(Tile tile, World world) {  //polymorphism
        int gain = switch (tile.getFoodType()) {
            case PLANKTON -> 10;
            case ALGAE -> 15;
            default -> 0;
        };
        if (getFoodLevel() + gain <= 100) {
            setFoodLevel(getFoodLevel() + gain);
            SimulationStatsManager.writeToFile(this.getName() + "," + this.getId() + ",eats,,," + tile.getFoodType() + "\n");
            tile.clearFood(world);
        }
    }



    /* -------------------------------GRAPHICS------------------------------- */

    private static Image babyImage;
    private static Image adultImage;

    private final ImageView imageView = new ImageView();
    @Override
    public ImageView getImageView() { return imageView; }

    /**
     * Loads animal images if not already loaded or if there is graphic environment.
     */
    private static void loadImagesIfNeeded() {
        if (babyImage == null || adultImage == null) {
            if (!GraphicsEnvironment.isHeadless()) {
                babyImage = new Image(Objects.requireNonNull(OceanicPuffer.class.getResource("/images/BabyOceanicPuffer1.png")).toExternalForm());
                adultImage = new Image(Objects.requireNonNull(OceanicPuffer.class.getResource("/images/AdultOceanicPuffer.png")).toExternalForm());
            }
        }
    }

    /**
     * Initializes graphical settings.
     */
    private void settings() {
        imageView.setPreserveRatio(true);
        updatePufferGraphics();
    }

    /**
     * Updates the graphical representation of the animal based on its age.
     */
    private void updatePufferGraphics() {
        loadImagesIfNeeded();
        boolean isYoung = getAge() < getMajority();
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
