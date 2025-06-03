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
public class Nemo extends Herbivorous {
    public Nemo(Coord position) {
        super(position, generateGenes());
        setName("Nemo");
        settings();
    }

    //konstruktor dziecka
    public Nemo(Coord position, Animal parent1, Animal parent2) {
        super(position, parent1, parent2);
        setName("Nemo");
        settings();
    }


    private static Genes generateGenes() {
        Genes genes = new Genes();
        genes.setStrength(10);
        genes.setSpeed(2);
        genes.setMaxAge(75);
        genes.setMaxLoneliness(40);
        genes.setMaxEnergy(80);
        return genes;
    }


    /* -------------------------------LIFE------------------------------- */

    public void update(World world) {  //polymorphism
        processLifeCycle(world);
        updateNemoGraphics();
        if (!isAlive()) { return; }

        Reproduction.pregnancyTick(world, this);

        tryToEat(world, this); //wywołanie mechaniki jedzenia
        tryToMate(world); //wywołanie mechaniki rozmnażania
        move(world); //wywołanie mechaniki ruchu
    }


    @Override
    public Animal giveBirth(Coord position, Animal parent1, Animal parent2) {  //polymorphism
        return new Nemo(position, parent1, parent2);
    }



    @Override
    public void eat(Tile tile, World world) {
        int gain = switch (tile.getFoodType()) {
            case PLANKTON -> 10;
            case ALGAE -> 15;
            default -> 0; //NONE
        };
        if (getFoodLevel()+gain <= 100){     //zjada o ile nie byłoby ponad 100 napchane
            setFoodLevel(getFoodLevel() + gain); //je
            SimulationStatsManager.writeToFile(this.getName() + "," + this.getId() + ",eats,,," + tile.getFoodType() + "\n");
            tile.clearFood(world);
        }
    }



    /* -------------------------------GRAPHICS------------------------------- */

    private static Image YoungNemo;
    private static Image OldNemo;

    private final ImageView imageView = new ImageView();
    @Override
    public ImageView getImageView() { return imageView; }


    private static void loadImagesIfNeeded() {
        if (YoungNemo == null || OldNemo == null) {
            if (!GraphicsEnvironment.isHeadless()) { //sprawdza czy jest srodowisko graficzne (testy go nie mają)
                YoungNemo = new Image(Objects.requireNonNull(Nemo.class.getResource("/images/YoungNemo.png")).toExternalForm());
                OldNemo = new Image(Objects.requireNonNull(Nemo.class.getResource("/images/OldNemo.png")).toExternalForm());
            }
        }
    }

    private void settings() {
        imageView.setPreserveRatio(true);
        updateNemoGraphics();
    }

    private void updateNemoGraphics() {
        loadImagesIfNeeded();

        boolean isYoung = getAge() < getMajority();
        imageView.setImage(isYoung ? YoungNemo : OldNemo);

        double scale = isYoung ? 0.7 : 1.0;
        imageView.setFitWidth(Main.getTileSize() * scale);
        imageView.setFitHeight(Main.getTileSize() * scale);
    }
}