package ocean;

import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        //pobranie parametrów początkowych
        int width = 20;
        int height = 20;

        int noFood = 10;
        int noCoral = 5;

        System.out.println("Podaj liczbę zwierząt:");
        int noAnimals = scanner.nextInt();

        System.out.println("Podaj liczbę cykli symulacji:");
        int ticks = scanner.nextInt();

        //inicjalizacja świata i ustawień
        WorldSetup worldSetup = new WorldSetup();
        World world = new World(width, height, noFood, noCoral, noAnimals, ticks, worldSetup);

        //run simulation
        world.runSimulation(ticks);


        //......
    }
}
