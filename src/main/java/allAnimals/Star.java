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


    /* -------------------------------GENES------------------------------- */

    //do tworzenia genów w nowych - zakresy w losowych wartościah do zmiany
    //nie może potrzebować objektu by dzialac bo to ma tworzyć konstruktor (objekt) a nie byc uzywanym przez niego wiec static
    private static Genes generateGenes() {
        Genes genes = new Genes();
        genes.setStrength(5);
        genes.setSpeed(2);
        genes.setMaxAge(100 + World.random.nextInt(50));
        genes.setMaxLoneliness(40 + World.random.nextInt(20));
        genes.setMaxEnergy(80);
        return genes;
    }


    /* -------------------------------LIFE------------------------------- */

    public void update(World world) {
        processLifeCycle(world); //duperele o życiu
        updateNemoGraphics();
        if (!isAlive()) return;

        Reproduction.pregnancyTick(world, this);

        tryToEat(world, this); //wywołanie mechaniki jedzenia
        tryToMate(world); //wywołanie mechaniki rozmnażania
        move(world); //wywołanie mechaniki ruchu
    }


    @Override
    public Animal giveBirth(Coord position, Animal parent1, Animal parent2) {
        return new Star(position, parent1, parent2);
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
            SimulationStatsManager.writeToFile(this.getName() + " id: " + this.getId() + " eats " + tile.foodType);
            tile.clearFood(world);
        }
    }



    /* -------------------------------GRAPHICS------------------------------- */

    private static Image babyStar;
    private static Image adultStar;
    private static final int AGE_OLD = 18; // one turn = one month


    private final ImageView imageView = new ImageView();
    public ImageView getImageView() { return imageView; } // getter


    /* -------------------------------GUI------------------------------- */

    private static void loadImagesIfNeeded() {
        if (babyStar == null || adultStar == null) {
            if (!GraphicsEnvironment.isHeadless()) { //sprawdza czy jest srodowisko graficzne (testy go nie mają)
                babyStar = new Image(Objects.requireNonNull(Nemo.class.getResource("/images/babyStar.png")).toExternalForm());
                adultStar = new Image(Objects.requireNonNull(Nemo.class.getResource("/images/adultStar.png")).toExternalForm());
            }
        }
    }

    private void settings() {
        imageView.setPreserveRatio(true);
        updateNemoGraphics();
    }

    private void updateNemoGraphics() {
        loadImagesIfNeeded();

        boolean isYoung = getAge() < AGE_OLD;
        imageView.setImage(isYoung ? babyStar : adultStar);

        double scale = isYoung ? 0.9 : 1.0;
        imageView.setFitWidth(Main.getTileSize() * scale);
        imageView.setFitHeight(Main.getTileSize() * scale);
    }
}
