package allAnimals;

import body.Animal;
import body.WorldObject;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import map.Coord;
import ocean.Main;
import ocean.SimulationStatsManager;
import ocean.World;

import java.awt.*;
import java.util.Objects;

//inheritance from WorldObject
/**
 * Represents an ink cloud that temporarily stuns nearby Dolphins.
 * Inherits from WorldObject.
 */
public class InkCloud extends WorldObject {
    //encapsulation
    private int remainingTime = 2; //znika po 2 turach
    private final Animal target;


    public InkCloud(Coord position, Animal target) {
        super(position);
        this.target = target;
        if (target != null && target.getClass() == Dolphin.class && !((Dolphin) target).isStunned()) { //jesli nie jest aktualnie zatrzymany inny delfin
            ((Dolphin) target).setStunned(true);
        }
        setupGraphics();
    }


    /**
     * Updates the InkCloud’s lifecycle, decreasing its duration each turn.
     * When the duration expires, the ink effect is removed and the object disappears.
     * @param world The simulation world in which it happens.
     */
    @Override
    public void update(World world) {  //polymorphism
        remainingTime--;
        if (remainingTime <= 0 || !target.isAlive()) {
            //efekt znika – przywraca ruch
            if (target != null && target.getName().equals("Dolphin")) {
                ((Dolphin) target).setStunned(false);
                SimulationStatsManager.writeToFile("Ink effect ended for Dolphin id: " + target.getId() + "\n");
            }
            world.removeObject(this); //usuwa chmurke po okreslonym czasie
        }
    }


    public Animal getTarget() {
        return target;
    }



    /* -------------------------------GRAPHICS------------------------------- */

    private static Image inkImage;

    private final ImageView imageView = new ImageView();
    @Override
    public ImageView getImageView() { return imageView; }

    /**
     * Initializes graphical settings for the InkCloud.
     */
    private void setupGraphics() {
        if (inkImage == null && !GraphicsEnvironment.isHeadless()) {  //sprawdza czy jest srodowisko graficzne
            inkImage = new Image(Objects.requireNonNull(InkCloud.class.getResource("/images/Ink.png")).toExternalForm());
        }

        imageView.setImage(inkImage);
        imageView.setOpacity(0.6); //półprzeźroczystość
        imageView.setFitWidth(Main.getTileSize());//dopasowanie do kratki
        imageView.setFitHeight(Main.getTileSize());
        imageView.setPreserveRatio(true);//zachowanie proporcji
    }
}
