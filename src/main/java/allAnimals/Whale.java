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
public class Whale extends Herbivorous {
    public Whale(Coord position) {
        super(position, generateGenes());
        setName("Whale");
        settings();
    }

    //konstruktor dziecka
    public Whale(Coord position, Animal parent1, Animal parent2) {
        super(position, parent1, parent2);
        setName("Whale");
        settings();
    }


    private static Genes generateGenes() {
        Genes genes = new Genes();
        genes.setStrength(Genes.mutate(50));
        genes.setSpeed(2);
        genes.setMaxAge(Genes.mutate(80));
        genes.setMaxLoneliness(Genes.mutate(70));
        genes.setMaxEnergy(80);
        return genes;
    }


    @Override
    public void update(World world) {  //polymorphism
        processLifeCycle(world);
        updateWhaleGraphics();
        if (!isAlive()) { return; }

        Reproduction.pregnancyTick(world, this);

        tryToEat(world, this); //wywołanie mechaniki jedzenia
        tryToMate(world); //wywołanie mechaniki rozmnażania
        move(world); //wywołanie mechaniki ruchu
    }


    @Override
    public Animal giveBirth(Coord position, Animal parent1, Animal parent2) {  //polymorphism
        return new Whale(position, parent1, parent2);
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

    private static Image youngWhale;
    private static Image oldWhale;

    private final ImageView imageView = new ImageView();
    @Override
    public ImageView getImageView() { return imageView; }


    private static void loadImagesIfNeeded() {
        if (youngWhale == null || oldWhale == null) {
            if (!GraphicsEnvironment.isHeadless()) { //sprawdza czy jest srodowisko graficzne (testy go nie mają)
                youngWhale = new Image(Objects.requireNonNull(Whale.class.getResource("/images/barrierBabyWhale.png")).toExternalForm());
                oldWhale = new Image(Objects.requireNonNull(Whale.class.getResource("/images/barrierWhale.png")).toExternalForm());
            }
        }
    }

    private void settings() {
        imageView.setPreserveRatio(true);
        updateWhaleGraphics();
    }

    private void updateWhaleGraphics() {
        loadImagesIfNeeded();

        boolean isYoung = getAge() < getMajority();
        imageView.setImage(isYoung ? youngWhale : oldWhale);

        double scale = isYoung ? 0.8 : 1.0;
        imageView.setFitWidth(Main.getTileSize() * 3 * scale);
        imageView.setFitHeight(Main.getTileSize() * 3 * scale);
    }
}