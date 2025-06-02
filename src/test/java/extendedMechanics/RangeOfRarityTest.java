package extendedMechanics;

import static org.junit.jupiter.api.Assertions.*;

import ocean.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


class RangeOfRarityTest {

    @BeforeEach
    void setup() {
        World.random = new Random();
    }


    @Test
    void testRandomRarityGeneration() { //czy zwraca dobre wartości
        RangeOfRarity rarityGenerator = new RangeOfRarity(); //tworzy instacje
        Rarity rarity = rarityGenerator.animalsDrawingByRarity(); //wywoluje metode która losuje rzadkość
        assertTrue(List.of(Rarity.RARE, Rarity.SUPER_RARE, Rarity.MYTHIC).contains(rarity)); //sprawdza czy mieści się w tych wartościach
    }


    @Test
    void testRarityDistribution() { //czy dobry rozkład
        RangeOfRarity rangeOfRarity = new RangeOfRarity();
        Map<Rarity, Integer> counts = new HashMap<>();

        int iterations = 1000000;
        double deviation = 0.002; //X% odchylenia zakładane

        for (Rarity rarity : Rarity.values()) {counts.put(rarity, 0);}  //ustawianie values na zero

        for (int i = 0; i < iterations; i++) {  //liczenie losowań
            Rarity rarity = rangeOfRarity.animalsDrawingByRarity();
            counts.put(rarity, counts.get(rarity) + 1); //zwiekszanie o 1 dla key
        }

        //obliczanie prawdopdodobienstwa (liczba wystapien)/(liczba losowań lub omega??)
        double mythic = counts.get(Rarity.MYTHIC) / (double) iterations;
        double superRare = counts.get(Rarity.SUPER_RARE) / (double) iterations;
        double rare = counts.get(Rarity.RARE) / (double) iterations;

        //porównywanie z oczekiwanymi wartościami (błąd +/- deviation)
        assertEquals(0.04, mythic, deviation);
        assertEquals(0.11, superRare, deviation);
        assertEquals(0.84, rare, deviation);
    }
}

