package allAnimals;

import body.WorldObject;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import map.Coord;
import ocean.Main;
import ocean.World;

import java.awt.*;
import java.util.Objects;

//inheritance from WorldObject
/**
 * Represents a Skeleton that remains visible for a certain number of simulation turns before decomposing.
 * Inherits from WorldObject.
 */
public class Skeleton extends WorldObject {
    //encapsulation
    private int decompositionTime = 5; // czas w turach, wyznaczający ile czasu będzie się wyświetlał szkielet

    /* -------------------------------KONSTUKTOR------------------------------- */

    public Skeleton(Coord position) {
        super(position);
        settings();
    }


    /* -------------------------------MECHANIKA------------------------------- */


    /**
     * Updates Skeleton's lifecycle each turn.
     * Gradually decomposes over time and is removed when decomposition time reaches zero.
     * @param world The simulation world in which it happens.
     */
    @Override
    public void update(World world) {   //polymorphism
        // czas rozkładu szkieletu, co turę się zmniejsza, finalnie się usunie
        decompositionTime--; // dekrementacja
        if (decompositionTime == 0) {
            world.removeObject(this); // usunięcie szkieletu
        }
    }


    /* -------------------------------GRAPHICS------------------------------- */

    private static Image SkeletonImage;

    private final ImageView imageView = new ImageView();
    @Override
    public ImageView getImageView() { return imageView; }


    /**
     * Loads skeleton image if not already loaded or if there is graphic environment.
     */
    private static void loadImagesIfNeeded() {
        if (SkeletonImage == null) {
            if (!GraphicsEnvironment.isHeadless()) { //sprawdza czy jest srodowisko graficzne (testy go nie mają)
                SkeletonImage = new Image(Objects.requireNonNull(Skeleton.class.getResource("/images/Skeleton.png")).toExternalForm());
            }
        }
    }

    /**
     * Initializes graphical settings.
     */
    private void settings() {
        loadImagesIfNeeded();

        imageView.setImage(SkeletonImage);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(Main.getTileSize() * 0.9);
        imageView.setFitHeight(Main.getTileSize() * 0.9);
    }
}
