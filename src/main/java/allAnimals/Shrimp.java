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
public class Shrimp extends Herbivorous {
    public Shrimp(Coord position) {
        super(position, generateGenes());
        setName("Shrimp");
        settings();
    }

    //konstruktor dziecka
    public Shrimp(Coord position, Animal parent1, Animal parent2) {
        super(position, parent1, parent2);
        setName("Shrimp");
        settings();
    }


    private static Genes generateGenes() {
        Genes genes = new Genes();
        genes.setStrength(5);
        genes.setSpeed(1);
        genes.setMaxAge(45);
        genes.setMaxLoneliness(30);
        genes.setMaxEnergy(80);
        return genes;
    }


    /* -------------------------------LIFE------------------------------- */

    public void update(World world) {  //polymorphism
        processLifeCycle(world);
        updateShrimpGraphics();
        if (!isAlive()) { return; }

        Reproduction.pregnancyTick(world, this);

        tryToEat(world, this); //wywołanie mechaniki jedzenia
        tryToMate(world); //wywołanie mechaniki rozmnażania
        move(world); //wywołanie mechaniki ruchu
    }


    @Override
    public Animal giveBirth(Coord position, Animal parent1, Animal parent2) {  //polymorphism
        return new Shrimp(position, parent1, parent2);
    }


    @Override
    public void eat(Tile tile, World world) {  //polymorphism
        int gain = switch (tile.getFoodType()) {
            case PLANKTON -> 10;
            case ALGAE -> 15;
            default -> 0; //NONE
        };
        if (getFoodLevel()+gain <= 100){ //zjada o ile nie byłoby ponad 100 napchane
            setFoodLevel(getFoodLevel() + gain); //je
            SimulationStatsManager.writeToFile(this.getName() + "," + this.getId() + ",eats,,," + tile.getFoodType() + "\n");
            tile.clearFood(world);
        }
    }



    /* -------------------------------GRAPHICS------------------------------- */

    private static Image babyShrimp;
    private static Image adultShrimp;

    private final ImageView imageView = new ImageView();
    @Override
    public ImageView getImageView() { return imageView; }


    private static void loadImagesIfNeeded() {
        if (babyShrimp == null || adultShrimp == null) {
            if (!GraphicsEnvironment.isHeadless()) { //sprawdza czy jest srodowisko graficzne (testy go nie mają)
                babyShrimp = new Image(Objects.requireNonNull(Nemo.class.getResource("/images/babyShrimp.png")).toExternalForm());
                adultShrimp = new Image(Objects.requireNonNull(Nemo.class.getResource("/images/adultShrimp.png")).toExternalForm());
            }
        }
    }

    private void settings() {
        imageView.setPreserveRatio(true);
        updateShrimpGraphics();
    }

    private void updateShrimpGraphics() {
        loadImagesIfNeeded();

        boolean isYoung = getAge() < getMajority();
        imageView.setImage(isYoung ? babyShrimp : adultShrimp);

        double scale = isYoung ? 0.6 : 0.8;
        imageView.setFitWidth(Main.getTileSize() * scale);
        imageView.setFitHeight(Main.getTileSize() * scale);
    }
}
