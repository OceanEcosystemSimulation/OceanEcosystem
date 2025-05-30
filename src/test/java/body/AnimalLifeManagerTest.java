package body;

import allAnimals.Nemo;
import allAnimals.Shark;
import map.Coord;
import ocean.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class AnimalLifeManagerTest {
    private World world;
    private Nemo nemo;
    private Shark shark;

    @BeforeEach
    void setup() {
        World.random = new Random();
        world = new World(10, 10, 0, 0, 1);
        nemo = new Nemo(new Coord(1, 1));
        shark = new Shark(new Coord(2, 1));
        world.addObject(nemo);
    }
    
    
    @Test
    void testAnimalDiesFromStarvation() { //czy umiera kiedy foodLevel<=0 + czy dobrze odejmuje
        nemo.setFoodLevel(0); //obnizenie poziomu jedzenia do 0
        AnimalLifeManager.lifeCycle(world, nemo);
        shark.setFoodLevel(1);
        AnimalLifeManager.lifeCycle(world, shark);

        assertFalse(nemo.isAlive()); //nie może żyć bo było 0
        assertFalse(shark.isAlive()); //nie może żyć bo powinno zrobić -1 na początku
    }


    @Test
    void testAnimalDiesFromOldAge() {  //czy umiera gdy age>maxAge + czy dobrze dodaje
        nemo.setAge(nemo.getGenes().getMaxAge() + 1);
        AnimalLifeManager.lifeCycle(world, nemo);
        shark.setAge(shark.getGenes().getMaxAge());
        AnimalLifeManager.lifeCycle(world, shark);

        assertFalse(nemo.isAlive()); //nie może żyć bo przekracza
        assertFalse(shark.isAlive()); //nie może żyć bo powinno zrobić +1 na początku
    }


    @Test
    void testAnimalHealthDecrease() {  //czy się zmniejsza health dobrze
        nemo.setLoneliness(nemo.getGenes().getMaxLoneliness());
        nemo.setFoodLevel(20); //jakieś super niskie wartości by nie robiło później przywracania zdrowia
        nemo.setEnergy(10);
        AnimalLifeManager.lifeCycle(world, nemo);

        shark.setFoodLevel(10); //niskie tak by odjeło z tego powodu
        shark.setLoneliness(0);
        shark.setEnergy(5);
        AnimalLifeManager.lifeCycle(world, shark);

        assertEquals(95, nemo.getHealth()); //powinno spaść o 5 ze startowych 100 bo loneliness>=maxLoneliness
        assertEquals(95, shark.getHealth()); //powinno spaść o 5 ze startowych 100 no foodLevel<40
    }
    

    @Test
    void testAnimalHealthRestores() { //czy zdrowie się odnawia dobrze
        nemo.setHealth(50);
        nemo.setFoodLevel(80); //>70
        nemo.setEnergy(30); //>20
        AnimalLifeManager.lifeCycle(world, nemo);

        shark.setHealth(96); //coś dużego że da >100 przy 120%
        shark.setFoodLevel(100);
        shark.setEnergy(21);
        shark.setLoneliness(0);
        AnimalLifeManager.lifeCycle(world, shark);
        
        assertEquals(60, nemo.getHealth()); //odnawia - 120% aktualnego
        assertEquals(100, shark.getHealth()); //jak 120% przekracza 100 to powinno dać 100
    }


    @Test
    void testUpdateEnergy() { //czy dobrze ustawia energie
        nemo.setEnergy(10);
        nemo.setFoodLevel(10); //jakieś niskie wartości by weszło do krytycznego poziomu (baseEnergy <20)
        nemo.setHealth(5);
        AnimalLifeManager.lifeCycle(world, nemo);

        shark.setFoodLevel(71); //jakieś wysokie wartości by nie było krytycznego poziomu
        shark.setHealth(60);
        AnimalLifeManager.lifeCycle(world, shark);

        assertEquals(6, nemo.getEnergy());
        assertEquals(67, shark.getEnergy());
    }



    @Test
    void testUpdateLoneliness() {
        Nemo nemo2 = new Nemo(new Coord(1, 2));
        nemo.setLoneliness(10);
        nemo2.setLoneliness(10);
        shark.setLoneliness(10);

        world.addObject(nemo);
        world.addObject(nemo2);
        AnimalLifeManager.lifeCycle(world, nemo);
        AnimalLifeManager.lifeCycle(world, shark);

        assertEquals(0, nemo.getLoneliness()); //powinno zerować obu nemo
        assertEquals(0, nemo2.getLoneliness());
        assertTrue(nemo2.getLonelinessReseted()); //powinno zmienić
        assertEquals(15, shark.getLoneliness()); //powinno zwiekszyc bo nie ma innego shark w okolicy - zerować też nie bo nie to samo co nemo
        assertFalse(shark.getLonelinessReseted()); //nie powinno zmienić
    }
}
