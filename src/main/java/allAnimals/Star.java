package allAnimals;

import extendedMechanics.Reproduction;
import map.Coord;
import map.Tile;
import ocean.*;
import body.Animal;
import body.Genes;
import body.Herbivorous;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.*;
import java.util.Objects;


//inheritance from Herbivorous
/**
 * Represents a Star, inheriting from Herbivorous.
 */
public class Star extends Herbivorous {
    public Star(Coord position) {
        super(position, generateGenes());
        setName("Star");
        settings();
    }

    //konstruktor dziecka
    public Star(Coord position, Animal parent1, Animal parent2) {
        super(position, parent1, parent2);
        setName("Star");
        settings();
    }


    /**
     * Generates default genetic attributes.
     * @return The genes object containing attributes.
     */
    private static Genes generateGenes() {
        Genes genes = new Genes();
        genes.setStrength(5);
        genes.setSpeed(1);
        genes.setMaxAge(99);
        genes.setMaxLoneliness(90);
        genes.setMaxEnergy(80);
        return genes;
    }


    /* -------------------------------LIFE------------------------------- */

    public void update(World world) {  //polymorphism
        processLifeCycle(world);
        updateStarGraphics();
        if (!isAlive()) { return;}

        Reproduction.pregnancyTick(world, this);

        tryToEat(world, this); //wywołanie mechaniki jedzenia
        tryToMate(world); //wywołanie mechaniki rozmnażania
        move(world); //wywołanie mechaniki ruchu
    }


    @Override
    public Animal giveBirth(Coord position, Animal parent1, Animal parent2) {  //polymorphism
        return new Star(position, parent1, parent2);
    }



    @Override
    public void eat(Tile tile, World world) {  //polymorphism
        int gain = switch (tile.getFoodType()) {
            case PLANKTON -> 10;
            case ALGAE -> 15;
            default -> 0; //NONE
        };
        if (getFoodLevel()+gain <= 100){
            setFoodLevel(getFoodLevel() + gain); //je
            SimulationStatsManager.writeToFile(this.getName() + "," + this.getId() + ",eats,,," + tile.getFoodType() + "\n");
            tile.clearFood(world);
        }
    }



    /* -------------------------------GRAPHICS------------------------------- */

    private static Image babyStar;
    private static Image adultStar;

    private final ImageView imageView = new ImageView();
    @Override
    public ImageView getImageView() { return imageView; }


    /**
     * Loads animal images if not already loaded or if there is graphic environment.
     */
    private static void loadImagesIfNeeded() {
        if (babyStar == null || adultStar == null) {
            if (!GraphicsEnvironment.isHeadless()) { //sprawdza czy jest srodowisko graficzne (testy go nie mają)
                babyStar = new Image(Objects.requireNonNull(Nemo.class.getResource("/images/babyStar.png")).toExternalForm());
                adultStar = new Image(Objects.requireNonNull(Nemo.class.getResource("/images/adultStar.png")).toExternalForm());
            }
        }
    }

    /**
     * Initializes graphical settings.
     */
    private void settings() {
        imageView.setPreserveRatio(true);
        updateStarGraphics();
    }

    /**
     * Updates the graphical representation of the animal based on its age.
     */
    private void updateStarGraphics() {
        loadImagesIfNeeded();

        boolean isYoung = getAge() < getMajority();
        imageView.setImage(isYoung ? babyStar : adultStar);

        double scale = isYoung ? 0.9 : 1.0;
        imageView.setFitWidth(Main.getTileSize() * scale);
        imageView.setFitHeight(Main.getTileSize() * scale);
    }
}
