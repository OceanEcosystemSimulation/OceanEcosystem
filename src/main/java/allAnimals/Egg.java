package allAnimals;

import body.Animal;
import body.Genes;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import map.Coord;
import ocean.Main;
import ocean.World;

import java.awt.*;
import java.util.Objects;

//inheritance from Animal
/**
 * Represents an Egg that will hatch into an animal after a certain number of simulation turns.
 * Inherits from Animal.
 */
public class Egg extends Animal {
    //encapsulation
    private int hatching;
    private Animal mother;

    /* -------------------------------CONSTRUCTOR------------------------------- */

    public Egg(Coord position, Genes genes, int hatching) {
        super(position, genes);
        this.hatching = hatching;
        settings();
    }


    /* -------------------------------MECHANICS------------------------------- */

    /**
     * Updates the Egg's lifecycle, decrementing its hatching counter.
     * Once hatching reaches zero, a baby animal is created and the Egg disappears.
     * @param world The simulation world in which it happens.
     */
    @Override
    public void update(World world) {  //polymorphism
        hatching--;
        if (hatching == 0) {
            Animal baby = giveBirth(getPosition(), mother, mother.getFatherDuringPregnancy());
            world.addObject(baby);
            this.die(world);
        }
    }


    /**
     * Spawns a baby animal based on the mother's species.
     * @param position The birth position.
     * @param parent1 The first parent.
     * @param parent2 The second parent.
     * @return A new instance of the mother's species.
     */
    @Override
    public Animal giveBirth(Coord position, Animal parent1, Animal parent2) {  //polymorphism – calling giveBirth will invoke the method specific to the mother's species
        return mother.giveBirth(position, parent1, parent2);
    }

    public void setMother(Animal mother) {
        this.mother = mother;
    }


    /* -------------------------------GRAPHICS------------------------------- */

    private static Image EggImage;

    private final ImageView imageView = new ImageView();
    @Override
    public ImageView getImageView() { return imageView; }

    /**
     * Loads egg image if not already loaded or if there is graphic environment.
     */
    private static void loadImagesIfNeeded() {
        if (EggImage == null) {
            if (!GraphicsEnvironment.isHeadless()) { //sprawdza czy jest srodowisko graficzne (testy go nie mają)
                EggImage = new Image(Objects.requireNonNull(Egg.class.getResource("/images/Egg.png")).toExternalForm());
            }
        }
    }

    /**
     * Initializes graphical settings.
     */
    private void settings() {
        loadImagesIfNeeded();

        imageView.setImage(EggImage);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(Main.getTileSize() * 0.9); // zmniejszenie - szerokosc
        imageView.setFitHeight(Main.getTileSize() * 0.9); // zmniejszenie - wysokosc
    }
}
