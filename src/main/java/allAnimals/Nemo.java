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
import java.util.Objects;
import java.util.List;


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


    /* -------------------------------GENES------------------------------- */

    //do tworzenia genów w nowych - zakresy w losowych wartościah do zmiany
    //nie może potrzebować objektu by dzialac bo to ma tworzyć konstruktor (objekt) a nie byc uzywanym przez niego wiec static
    private static Genes generateGenes() {
        Genes genes = new Genes();
        genes.setStrength(5 + rand.nextInt(5));
        genes.setSpeed(3);
        genes.setMaxAge(100 + rand.nextInt(50));
        genes.setMaxLoneliness(40 + rand.nextInt(20));
        genes.setMaxEnergy(80);
        return genes;
    }


    /* -------------------------------LIFE------------------------------- */

    public void update(World world) {
        processLifeCycle(world); //duperele o życiu
        updateNemoGraphics();
        if (!isAlive()) return;

        Reproduction.pregnancyTick(world, this);

        tryToEat(world); //wywołanie mechaniki jedzenia

        //sprawdza czy może się rozmnażać
        List<Animal> mates = WorldSearch.allMatesInRange(world, this, this.getPosition(), this.getGenes().getSpeed());
        for (Animal mate : mates) { //przechodzi po każdym mate
            if (mate != null) {
                if (Reproduction.isDistance(this, mate)) { //jeżeli są w kratkach obok
                    Reproduction.ReproductionProcess(this, mate); //mechanika reprodukcji
                } else {
                    boolean move = Reproduction.moveToMate(this, mate, world);
                    if (move && Reproduction.isDistance(this, mate)) { //czy się przesunął i na wszelki czy mate jest obok
                        Reproduction.ReproductionProcess(this, mate); //mechanika reprodukcji
                    }
                }
            }
        }

        move(world); //wywołanie mechaniki ruchu
    }


    @Override
    public Animal giveBirth(Coord position, Animal parent1, Animal parent2) {
        return new Nemo(position, parent1, parent2);
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



    /* -------------------------------GRAPHICS------------------------------- */

    // Nemo < 18

    private static final Image YoungNemo = new Image(Objects.requireNonNull(Nemo.class.getResource("/images/YoungNemo.png")).toExternalForm());

    // Nemo >= 18

    private static final int AGE_OLD = 18; // one turn = one month

    private static final Image OldNemo = new Image(Objects.requireNonNull(Nemo.class.getResource("/images/OldNemo.png")).toExternalForm());
    private final ImageView imageView = new ImageView();
    public ImageView getImageView() {
        return imageView;
    } // getter


    /* -------------------------------GUI------------------------------- */

    private void settings() {
        imageView.setPreserveRatio(true);
        updateNemoGraphics();
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
}