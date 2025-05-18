package map;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.List;


class CoordTest {

    @Test
    void testDistance() {  //czy dobrze liczy distance
        Coord a = new Coord(3, 4);
        Coord b = new Coord(7, 1);

        assertEquals(4, a.distance(b));  // max( |3-7|, |4-1| ) = max(4, 3) = 4
    }


    @Test
    void testGetAdjacentCoords() {  //czy zwraca dobra liste sasiadów
        Coord center = new Coord(5, 5); //przykladowy srodek
        List<Coord> neighbors = Coord.getAdjacentCoords(center); //lista jego sasiadow

        assertEquals(8, neighbors.size()); //powinno byc ich 8 na liście

        boolean contains44 = false;
        for (Coord c : neighbors) {
            if (c.getX() == 4 && c.getY() == 4) {
                contains44 = true;
                break;
            }
        }
        assertTrue(contains44); //czy zawiera przykładowe (4,4)

        boolean containsCenter = false;
        for (Coord c : neighbors) {
            if (c.getX() == 5 && c.getY() == 5) {
                containsCenter = true;
                break;
            }
        }
        assertFalse(containsCenter); //sprawdza czy nie liczy środka
    }
}


