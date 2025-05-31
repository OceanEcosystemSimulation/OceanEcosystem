package allAnimals;

import body.WorldObject;
import map.Coord;
import ocean.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Random;

public class OctopusTest {
    private World world;
    private Octopus octopus;
    private Dolphin dolphin;

    @BeforeEach
    void setup() {
        World.random = new Random();
        world = new World(10, 10, 0, 0, 0);
        octopus = new Octopus(new Coord(1, 1));
        dolphin = new Dolphin(new Coord(1, 2));
    }

    @Test
    public void testReleaseInkWhenDolphinNearby() {
        world.addObject(octopus);
        world.addObject(dolphin);
        octopus.update(world);
        List <WorldObject> objects = world.getObjects();

        assertEquals(3, objects.size()); //powinno byc nemo, delfin i inkcloud
        assertTrue(dolphin.isStunned()); //status delfina powinien się zmienic na stunned
    }


    @Test
    public void testInkCloudDisappearsAfterTwoTurns() {
        world.addObject(octopus);
        world.addObject(dolphin);
        octopus.update(world);
        octopus.setPosition(new Coord(10,10));

        List <WorldObject> objects = world.getObjects();
        assertEquals(3, objects.size()); //powinno byc nemo, delfin i inkcloud

        world.runSimulation(1);
        assertEquals(3, objects.size()); //powinno byc nemo, delfin i inkcloud

        world.runSimulation(1);
        assertEquals(2, objects.size()); //powinno byc nemo, delfin

        assertFalse(dolphin.isStunned()); //efekt powinien przestac dzialac
    }
}

