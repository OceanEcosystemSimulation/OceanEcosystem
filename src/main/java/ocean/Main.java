package ocean;

import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Pobranie parametrów początkowych od użytkownika
        int width = 20;
        int height = 20;

        int noFood = 10;
        int noCoral = 5;

        System.out.println("Podaj liczbę zwierząt:");
        int noAnimals = scanner.nextInt();

        System.out.println("Podaj liczbę cykli symulacji:");
        int ticks = scanner.nextInt();

        // Inicjalizacja świata i ustawień
        WorldSetup worldSetup = new WorldSetup();
        World world = new World(width, height, noFood, noCoral, noAnimals, ticks, worldSetup);

        // Uruchomienie symulacji
        world.runSimulation(ticks);

        // Analiza wyników symulacji
        Map<String, Integer> speciesCount = new HashMap<>();
        for (Animal animal : world.getNearbyAnimals(new Coord(0, 0), Math.max(width, height))) {
            String species = animal.getClass().getSimpleName();
            speciesCount.put(species, speciesCount.getOrDefault(species, 0) + 1);
        }

        // Wyświetlenie wyników końcowych
        System.out.println("\nStan końcowy symulacji:");
        for (Map.Entry<String, Integer> entry : speciesCount.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        scanner.close();
    }
}
