package allAnimals;

import body.*;
import extendedMechanics.Reproduction;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import map.*;
import ocean.Main;
import ocean.World;

import java.awt.*;
import java.util.List;
import java.util.Objects;


public class Shark extends Carnivorous {
    public Shark(Coord position) {
        super(position, generateGenes());
        setName("Shark");
        settings();

    }

    //konstruktor dziecka
    public Shark(Coord position, Animal parent1, Animal parent2) {
        super(position, parent1, parent2);
        setName("Shark");
        settings();
    }


    /* -------------------------------GENES------------------------------- */

    //do tworzenia genów w nowych
    private static Genes generateGenes() {
        Genes genes = new Genes();
        genes.setStrength(Genes.mutate(40));
        genes.setSpeed(5);
        genes.setMaxAge(Genes.mutate(89));
        genes.setMaxLoneliness(Genes.mutate(40));
        genes.setMaxEnergy(Genes.mutate(80));
        return genes;
    }


    /* -------------------------------LIFE------------------------------- */

    @Override
    public void update(World world) {
        processLifeCycle(world); //duperele o życiu
        updateSharkGraphics();
        if (!isAlive()) return;

        Reproduction.pregnancyTick(world, this);

        tryToMate(world); //wywołanie mechaniki rozmnażania
        move(world); //wywołanie mechaniki ruchu
        tryToAttack(world, this); //wywołanie mechaniki ataku
    }

    // tylko tata, bo kobieta rodzi
    @Override
    public Animal giveBirth(Coord position, Animal parent1, Animal parent2) {
        return new Shark(position, parent1, parent2);
    }



    //stwierdziłam że dam tak bo bez sensu sie ma robić ciągle od nowa jak jest niezmienna
    private static final List<String> preyList = List.of("Nemo"); //lista kogo atakuje - do zmiany wartości (dodawane po przecinku jak coś)


    @Override
    public boolean canAttack(Animal other) {
        return other != null && other.getName() != null && preyList.contains(other.getName());
    }

    //przykładowe to wpisywania ile jakie jedzenie daje
    //można zrobić ifem jak wcześniej było jak wam nie pasuje takie
    @Override
    public int calculateGain(Animal animal) {
        return switch (animal.getName()) {
            case "Nemo" -> 30;
            //itd inne
            default -> 0;
        };
    }


    /* -------------------------------GRAPHICS------------------------------- */

    private static Image YoungShark;
    private static Image OldShark;
    private static final int AGE_OLD = 18; // one turn = one month

    private final ImageView imageView = new ImageView();
    public ImageView getImageView() { return imageView; } // getter

    /* -------------------------------GUI------------------------------- */

    private static void loadImagesIfNeeded() {
        if (YoungShark == null || OldShark == null) {
            if (!GraphicsEnvironment.isHeadless()) { //sprawdza czy jest srodowisko graficzne (testy go nie mają)
                YoungShark = new Image(Objects.requireNonNull(Shark.class.getResource("/images/YoungShark.png")).toExternalForm());
                OldShark = new Image(Objects.requireNonNull(Shark.class.getResource("/images/OldShark.png")).toExternalForm());
            }
        }
    }

    private void settings() {
        imageView.setPreserveRatio(true);
        updateSharkGraphics();
    }

    private void updateSharkGraphics() {
        loadImagesIfNeeded();

        boolean isYoung = getAge() < AGE_OLD;
        imageView.setImage(isYoung ? YoungShark : OldShark);

        double scale = isYoung ? 0.8 : 1.0;
        imageView.setFitWidth(Main.getTileSize() * scale);
        imageView.setFitHeight(Main.getTileSize() * scale);
    }


}

