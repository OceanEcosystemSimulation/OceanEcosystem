package allAnimals;

import body.Animal;
import body.WorldObject;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import map.Coord;
import ocean.Main;
import ocean.World;

import java.awt.*;
import java.util.Objects;

public class InkCloud extends WorldObject {
    private static Image inkImage;
    private final ImageView imageView = new ImageView();
    private int remainingTime = 2; //znika po 2 turach
    private Animal target;

    public InkCloud(Coord position, Animal target) {
        super(position);
        this.target = target;
        //jesli nie jest aktualnie zatrzymany inny delfin
        if (target != null && target.getName().equals("Dolphin")) {
            ((Dolphin) target).setStunned(true);
        }

        setupGraphics();
    }


    private void setupGraphics() {
        if (inkImage == null && !GraphicsEnvironment.isHeadless()) {
            inkImage = new Image(Objects.requireNonNull(InkCloud.class.getResource("/images/Ink.png")).toExternalForm());
        }

        imageView.setImage(inkImage);
        imageView.setOpacity(0.6); //półprzeźroczystość
        imageView.setFitWidth(Main.getTileSize());//dopasowanie do kratki
        imageView.setFitHeight(Main.getTileSize());
        imageView.setPreserveRatio(true);//zachowanie proporcji
    }

    @Override
    public void update(World world) {
        remainingTime--;
        if (remainingTime <= 0) {
            // efekt znika – przywróć ruch
            if (target != null && target.getName().equals("Dolphin")) {
                ((Dolphin) target).setStunned(false);
                System.out.println("Ink effect ended for Dolphin id: " + target.getId());
            }
            world.removeObject(this);
        }
    }


    public ImageView getImageView() {
        return imageView;
    }
}
