package allAnimals;

import body.Gender;
import extendedMechanics.Reproduction;
import map.Coord;
import map.Tile;

import ocean.*;

import body.Animal;
import body.Genes;
import body.Herbivorous;

/* ------GRAPHICS------ */
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.awt.*;
import java.util.Objects;


/* Nemo - roślinożerna, urocza istotka */

public class Nemo extends Herbivorous {

    /* -------------------------------GRAPHICS------------------------------- */

    // Nemo < 18

    private static final Image YoungNemo = new Image(Objects.requireNonNull(Nemo.class.getResource("/images/YoungNemo.png")).toExternalForm());

    // Nemo >= 18

    private static final Image OldNemo = new Image(Objects.requireNonNull(Nemo.class.getResource("/images/OldNemo.png")).toExternalForm());

    private static final int AGE_OLD = 18; // one turn = one month

    private final ImageView imageView = new ImageView();

    public ImageView getImageView() {
        return imageView;
    } // getter

    /* -------------------------------GUI------------------------------- */

    private void settings() {
        imageView.setPreserveRatio(true);
        updateNemoGraphics();
        setName("Nemo");
    }

    private void updateNemoGraphics() {
        if (getAge() < AGE_OLD) {
            imageView.setImage(YoungNemo);
            imageView.setFitWidth(World.TILE_SIZE * 0.6);
            imageView.setFitHeight(World.TILE_SIZE * 0.6);
        } else {
            imageView.setImage(OldNemo);
            imageView.setFitWidth(World.TILE_SIZE);
            imageView.setFitHeight(World.TILE_SIZE);
        }
    }

    /* -------------------------------CONSTRUCTORS------------------------------- */

    // kontruktor losowej rybki

    public Nemo(Coord position, Genes genes) {
        super(position, genes,
                100 + rand.nextInt(50),
                40 + rand.nextInt(20),
                90 + rand.nextInt(40));
        //wartości maxAge i maxLoneliness do zmiany
        settings();
        setName("Nemo");
    }

    //kontruktor rodzica

    public Nemo(Coord position, Animal parent1, Animal parent2) {
        super(position, parent1, parent2);//konstruktor dziecka
        settings();
        setName("Nemo");
    }

    /* -------------------------------GENES------------------------------- */

    //do tworzenia genów w nowych - zakresy w losowych wartościah do zmiany
    //nie może potrzebować objektu by dzialac bo to ma tworzyć konstruktor (objekt) a nie byc uzywanym przez niego wiec static
    private static Genes generateGenes() {
        Genes g = new Genes();
        g.setStrength(5 + rand.nextInt(5));
        g.setSpeed(10 + rand.nextInt(10));
        g.setFertility(20 + rand.nextInt(10));
        g.setGender(rand.nextBoolean() ? Gender.FEMALE : Gender.MALE);
        return g;
    }

    /* -------------------------------LIFE------------------------------- */

    public void update(World world) {
        processLifeCycle(world); //duperele o życiu
        updateNemoGraphics();
        if (!isAlive()) return;

        Reproduction.pregnancyTick(world, this);

        tryToEat(world); //wywołanie mechaniki jedzenia
        int radius = getGenes().getSpeed();
        Animal mate = WorldSearch.nearestMate(world, getPosition(), radius, this);
        Reproduction.ReproductionProcess(world, this, mate);
        move(world); //wywołanie mechaniki ruchu
    }

    // tylko tata, bo kobieta rodzi
    @Override
    public Animal giveBirth(Coord position, Genes genes) {
        return new Nemo(position, genes);
    }

    /* -------------------------------EATING------------------------------- */

    //zjada o ile nie byłoby ponad 100 napchane
    @Override
    public void eat(Tile tile) { //przykładowe jak pisać
        int gain = switch (tile.foodType) {
            case PLANKTON -> 10;
            case ALGAE -> 15;
            default -> 0; //NONE
        };
        if (getAge()+gain <= 100){
            setFoodLevel(getFoodLevel() + gain); //je
            System.out.println(this.getName() + " id: " + this.getId() + " eats " + tile.foodType);
            tile.clearFood();
        }
    }
}