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

public class Egg extends Animal {

    /* -------------------------------STAŁE------------------------------- */

    private int hatching;
    private Animal mother;

    /* -------------------------------KONSTUKTOR------------------------------- */

    public Egg(Coord position, Genes genes, int hatching) {
        super(position, genes);
        this.hatching = hatching;
        settings();
    }

    /* -------------------------------GRAFIKI------------------------------- */

    private static Image EggImage;

    private final ImageView imageView = new ImageView();

    private static void loadImagesIfNeeded() {
        if (EggImage == null) {
            if (!GraphicsEnvironment.isHeadless()) { //sprawdza czy jest srodowisko graficzne (testy go nie mają)
                EggImage = new Image(Objects.requireNonNull(Egg.class.getResource("/images/Egg.png")).toExternalForm());
            }
        }
    }

    private void settings() {
        loadImagesIfNeeded();

        imageView.setImage(EggImage);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(Main.getTileSize() * 0.9); // zmniejszenie - szerokosc
        imageView.setFitHeight(Main.getTileSize() * 0.9); // zmniejszenie - wysokosc
    }

    @Override
    public ImageView getImageView() { return imageView; }


    /* -------------------------------MECHANIKA------------------------------- */

    @Override
    public void update(World world) {
        hatching--;
        if (hatching == 0) {
            Animal baby = giveBirth(getPosition(), mother, mother.getFatherDuringPregnancy());
            world.addObject(baby);
            world.removeObject(this);
        }
    }

    @Override
    public Animal giveBirth(Coord position, Animal parent1, Animal parent2) {
        return mother.giveBirth(position, parent1, parent2);
    }

    public void setMother(Animal mother) {
        this.mother = mother;
    }
}
