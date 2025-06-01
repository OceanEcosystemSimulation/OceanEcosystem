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


    /* -------------------------------GENES------------------------------- */

    //do tworzenia genów w nowych - zakresy w losowych wartościah do zmiany
    //nie może potrzebować objektu by dzialac bo to ma tworzyć konstruktor (objekt) a nie byc uzywanym przez niego wiec static
    private static Genes generateGenes() {
        Genes genes = new Genes();
        genes.setStrength(Genes.mutate(5));
        genes.setSpeed(1);
        genes.setMaxAge(Genes.mutate(45));
        genes.setMaxLoneliness(Genes.mutate(30));
        genes.setMaxEnergy(Genes.mutate(80));
        return genes;
    }


    /* -------------------------------LIFE------------------------------- */

    public void update(World world) {
        processLifeCycle(world); //duperele o życiu
        updateShrimpGraphics();
        if (!isAlive()) return;

        Reproduction.pregnancyTick(world, this);

        tryToEat(world, this); //wywołanie mechaniki jedzenia
        tryToMate(world); //wywołanie mechaniki rozmnażania
        move(world); //wywołanie mechaniki ruchu
    }


    @Override
    public Animal giveBirth(Coord position, Animal parent1, Animal parent2) {
        return new Shrimp(position, parent1, parent2);
    }


    /* -------------------------------EATING------------------------------- */

    //zjada o ile nie byłoby ponad 100 napchane
    @Override
    public void eat(Tile tile, World world) {
        int gain = switch (tile.foodType) {
            case PLANKTON -> 10;
            case ALGAE -> 15;
            default -> 0; //NONE
        };
        if (getFoodLevel()+gain <= 100){
            setFoodLevel(getFoodLevel() + gain); //je
            SimulationStatsManager.writeToFile(this.getName() + "," + this.getId() + ",eats,,," + tile.foodType + "\n");
            tile.clearFood(world);
        }
    }



    /* -------------------------------GRAPHICS------------------------------- */

    private static Image babyShrimp;
    private static Image adultShrimp;
    private static final int AGE_OLD = 18; // one turn = one month


    private final ImageView imageView = new ImageView();
    public ImageView getImageView() { return imageView; } // getter


    /* -------------------------------GUI------------------------------- */

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

        boolean isYoung = getAge() < AGE_OLD;
        imageView.setImage(isYoung ? babyShrimp : adultShrimp);

        double scale = isYoung ? 0.6 : 0.8;
        imageView.setFitWidth(Main.getTileSize() * scale);
        imageView.setFitHeight(Main.getTileSize() * scale);
    }
}
