package allAnimals;

import body.Animal;
import body.Genes;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import map.Coord;
import ocean.World;

import java.util.Objects;

public class Egg extends Animal {

    /* -------------------------------CONSTANTS------------------------------- */

    private final Genes genes;
    private int hatching;
    private Animal mother;

    /* -------------------------------CONSTRUCTOR------------------------------- */

    public Egg(Coord position, Genes genes, int hatching) {
        super(position, genes, 0, 0, 100);
        this.genes = genes;
        this.hatching = hatching;
        imageView.setImage(EggImage);
        imageView.setPreserveRatio(true);
    }

    /* -------------------------------GRAPHICS------------------------------- */

    private static final Image EggImage = new Image(Objects.requireNonNull(Egg.class.getResource("/images/Egg.png")).toExternalForm());

    private final ImageView imageView = new ImageView();

    public ImageView getImageView() {
        return imageView;
    } // getter


    /* -------------------------------MECHANICS------------------------------- */

    @Override
    public void update(World world) {
        hatching--;
        if (hatching == 0) {
            Animal baby = this.getMother().giveBirth(getPosition(), genes);
            world.addAnimal(baby);
            world.getAnimals().remove(this);
        }
    }

    @Override
    public Animal giveBirth(Coord position, Genes genes) {
        return mother.giveBirth(position, genes);
    }

    /*public Coord getPosition() {
        return position;
    }*/

    public void setMother(Animal mother) {
        this.mother = mother;
    }

    public Animal getMother() {
        return mother;
    }
}
