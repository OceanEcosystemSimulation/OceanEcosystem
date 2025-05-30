package allAnimals;

import body.Genes;
import map.Coord;
import map.FoodType;
import map.MapType;
import map.Tile;
import ocean.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;



public class NemoTest {
    private World world;

    @BeforeEach
    void setup() {
        World.random = new Random();
        world = new World(10, 10, 0, 0, 0);
    }

    @Test
    void testNemoIsCreatedCorrectly() { //czy został poprawnie utworzony i czy wartości są poprawne
        Coord position = new Coord(3, 5);
        Nemo nemo = new Nemo(position); //tworzy nemo z konkretna pozycja

        assertNotNull(nemo); //sprawdza czy nemo nie jest null - czy stworzony
        assertEquals(position.getX(), nemo.getPosition().getX()); //czy ma dobre x
        assertEquals(position.getY(), nemo.getPosition().getY()); //czy ma dobre y
        assertEquals("Nemo", nemo.getName()); //czy ma dobre imie nadane
        assertTrue(nemo.isAlive()); //czy żyje na starcie
    }

    @Test
    void testNemoGenes() {
        Nemo nemo = new Nemo(new Coord(2, 2)); //tworzy nemo z konkretna pozycja
        Genes genes = nemo.getGenes(); //pobiera geny

        assertNotNull(genes); //sprawdza czy geny nie null
        assertTrue(genes.getStrength() >= 8 && genes.getStrength() <= 12); //10 +-2 z możliwej mutacji
        assertTrue(genes.getSpeed() == 1 || genes.getSpeed() == 2);
        assertTrue(genes.getMaxAge() >= 73 && genes.getMaxAge() <= 77); //75 +-2 z możliwej mutacji
        assertTrue(genes.getMaxEnergy() >= 78 && genes.getMaxEnergy() <= 82); //80 +-2 z możliwej mutacji
    }


    @Test
    void testEatPlanktonIncreasesFoodLevel() { //czy dobrze dodaje jedzenie
        Nemo nemo = new Nemo(new Coord(0, 0)); //tworzenie nemo
        Tile tile = new Tile(0, 0, MapType.NORMAL); //tworzenie tile
        tile.setFoodType(FoodType.PLANKTON); //ustawienie pola na jedzenie
        int initialFood = nemo.getFoodLevel(); //pobieranie poziomu jedzenia przed zjedzeniem
        nemo.eat(tile, world); //nemo je

        assertEquals(initialFood, nemo.getFoodLevel()); //porównanie czy dobrze się zwiększyło (jest najedzony wiec powinno nic)
    }




    //te niżej są powtórką trochę po AnimalLifeManagerTest ale to głównie sprawdzenie czy update się dobrze łączył z lifeCycle i innymi



    @Test
    void testNemoDiesFromStarvation() {
        Nemo nemo = new Nemo(new Coord(1, 1));
        nemo.setFoodLevel(0); //obnizenie poziomu jedzenia do 0
        nemo.update(world); //wywołanie update

        assertFalse(nemo.isAlive()); //nie może żyć
    }


    @Test
    void testNemoDiesFromOldAge() {
        Nemo nemo = new Nemo(new Coord(2, 2));
        nemo.setAge(nemo.getGenes().getMaxAge() + 1); //wiek przekracza limit
        nemo.update(world);

        assertFalse(nemo.isAlive());
    }


    @Test
    void testLonelinessResetsWithSameSpeciesNearby() {  //czy resetuje loneliness u obu nemo które się spotkają
        Nemo nemo1 = new Nemo(new Coord(1, 1));
        nemo1.setLoneliness(10);
        Nemo nemo2 = new Nemo(new Coord(1, 2));
        nemo2.setLoneliness(10);

        world.addObject(nemo1);
        world.addObject(nemo2);
        nemo1.update(world);
        nemo2.update(world);

        assertEquals(0, nemo1.getLoneliness());
        assertEquals(0, nemo1.getLoneliness());
    }


    @Test
    void testNemoDiesFromHealthProblem() {
        Nemo nemo = new Nemo(new Coord(4, 4));
        nemo.setHealth(0);
        nemo.update(world);

        assertFalse(nemo.isAlive());
    }


    @Test
    void testUpdateDoesNothingIfDead() { //czy dobrze się zachowuje jak umrze
        Nemo nemo = new Nemo(new Coord(0, 0)); //tworzenie nemo
        nemo.die(world); //nemo umiera

        assertFalse(nemo.isAlive()); //czy serio martwy

        nemo.update(world); //update w nemo

        //czy się nie przesuwa jak nie zyje ?? szczerze nie wiedziałam jakie testy dać
        assertEquals(0, nemo.getPosition().getX()); //porównuje x do 0
        assertEquals(0, nemo.getPosition().getY()); //porównuje y do 0
    }
}

