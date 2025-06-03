package body;

import javafx.scene.image.ImageView;
import map.Coord;
import ocean.World;

//abstract class
/**
 * Abstract base class for all objects that exist within the simulation world.
 * Provides a position and requires implementation of update logic and graphical representation.
 */
public abstract class WorldObject {
    //encapsulation
    private Coord position; //aggregation

    public WorldObject(Coord position) {
        this.position = position;
    }

    /**
     * Updates the state or behavior of the object within the simulation.
     * @param world The simulation world in which it happens.
     */
    public abstract void update(World world);

    public abstract ImageView getImageView();


    public Coord getPosition() { return position; }
    public void setPosition(Coord newPosition) { this.position = newPosition; }
}

