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

public class Skeleton extends Animal {

    /* -------------------------------STAŁE------------------------------- */

    private int decompositionTime = 5; // czas w turach, wyznaczający ile czasu będzie się wyświetlał szkielet

    /* -------------------------------KONSTUKTOR------------------------------- */

    public Skeleton(Coord position) {
        super(position,  genesForSkeleton());
        setName("Skeleton");
        settings();
    }

    /* -------------------------------GRAFIKI------------------------------- */

    private static Image SkeletonImage;

    private final ImageView imageView = new ImageView();

    private static void loadImagesIfNeeded() {
        if (SkeletonImage == null) {
            if (!GraphicsEnvironment.isHeadless()) { //sprawdza czy jest srodowisko graficzne (testy go nie mają)
                SkeletonImage = new Image(Objects.requireNonNull(Skeleton.class.getResource("/images/Skeleton.png")).toExternalForm());
            }
        }
    }

    // dodanie grafiki
    private void settings() {
        loadImagesIfNeeded();

        imageView.setImage(SkeletonImage);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(Main.getTileSize() * 0.9);
        imageView.setFitHeight(Main.getTileSize() * 0.9);
    }

    public ImageView getImageView() { return imageView; } // getter


    /* -------------------------------MECHANIKA------------------------------- */

    private static Genes genesForSkeleton() {
        return null; // szkielet nie ma JUŻ genów :<
    }

    @Override
    public void update(World world) { // czas rozkładu szkieletu, co turę się zmniejsza, finalnie się usunie
        decompositionTime--; // dekrementacja
        if (decompositionTime == 0) {
            world.getAnimals().remove(this); // usunięcie szkieletu
        }
    }

    @Override
    public Animal giveBirth(Coord pos, Animal parent1, Animal parent2) {
        return null; // null, bo to szkielet
    }

}
