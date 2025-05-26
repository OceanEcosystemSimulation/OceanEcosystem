package allAnimals;

import body.WorldObject;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import map.Coord;
import ocean.Main;
import ocean.World;

import java.awt.*;
import java.util.Objects;

public class Skeleton extends WorldObject {

    /* -------------------------------STAŁE------------------------------- */

    private int decompositionTime = 5; // czas w turach, wyznaczający ile czasu będzie się wyświetlał szkielet

    /* -------------------------------KONSTUKTOR------------------------------- */

    public Skeleton(Coord position) {
        super(position);
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

    @Override
    public ImageView getImageView() { return imageView; } // getter


    /* -------------------------------MECHANIKA------------------------------- */

    @Override
    public void update(World world) { // czas rozkładu szkieletu, co turę się zmniejsza, finalnie się usunie
        decompositionTime--; // dekrementacja
        if (decompositionTime == 0) {
            world.removeObject(this); // usunięcie szkieletu
        }
    }
}
