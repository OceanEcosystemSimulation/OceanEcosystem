package allAnimals;

import body.Animal;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import map.Coord;
import ocean.Main;
import ocean.World;

import java.awt.*;
import java.util.Objects;

public class InkCloud extends Animal {
    private static Image inkImage;
    private final ImageView imageView = new ImageView();
    private int remainingTime = 3; //znika po 3 turach

    public InkCloud(Coord position) {
        super(position, null);
        setName("InkCloud");
        setupGraphics();
    }

    private void setupGraphics() {
        if (inkImage == null && !GraphicsEnvironment.isHeadless()) {
            inkImage = new Image(Objects.requireNonNull(InkCloud.class.getResource("/images/Ink.png")).toExternalForm());
        }

        imageView.setImage(inkImage);
        imageView.setOpacity(0.6);
        imageView.setFitWidth(Main.getTileSize());
        imageView.setFitHeight(Main.getTileSize());
        imageView.setPreserveRatio(true);
    }

    @Override
    public void update(World world) {
        remainingTime--;
        if (remainingTime <= 0) {
            world.getAnimals().remove(this);
        }
    }


    public ImageView getImageView() {
        return imageView;
    }

    @Override
    public Animal giveBirth(Coord position, Animal parent1, Animal parent2) {
        return null; //nie dotyczy
    }
}
