package body;

import javafx.scene.image.ImageView;
import map.Coord;
import ocean.World;

public abstract class WorldObject {
    private Coord position; //aggregation

    public WorldObject(Coord position) {
        this.position = position;
    }

    public abstract void update(World world);

    public abstract ImageView getImageView();


    public Coord getPosition() { return position; }
    public void setPosition(Coord newPosition) { this.position = newPosition; }
}

