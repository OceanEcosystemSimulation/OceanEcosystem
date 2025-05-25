package ocean;

import body.Animal;

import javafx.scene.control.Label;

import java.util.HashMap;
import java.util.Map;


public class SimulationStatsManager {
    private static final Map<String, Integer> speciesCount = new HashMap<>(); //tworzy HashMap: gatunek->ilość

    //liczenie zwierzat
    public static void updateSpeciesCount(World world) {
        speciesCount.clear();  //czyści mapę - można jak będzie dużo rzeczy to zmienić to na jesli nie zywe to -1 i usuwa (zmienic z runSimulation) ale przy kilkuset podobno powinno byc git
        for (Animal animal : world.getAnimals()) {
            if (animal.isAlive()) { //czy żywe - na wszelki
                speciesCount.put(animal.getName(), speciesCount.getOrDefault(animal.getName(), 0) + 1); //dodaje
                //pobiera nazwę, aktualną liczbę i powieksza o 1 - jesli nie ma jeszcze (deafult) to 0 (bo domysnie jest null wiec trzeba to tak
                //jak się nie podoba to getordefault to można ifem sprawdzac czy klucz istnieje containsKey i speciecCount.get...
            }
        }
    }


    //tekst do statystyk
    private static String animalStatsText() {
        String statsText = "";
        for (Map.Entry<String, Integer> entry : speciesCount.entrySet()) {
            if (!entry.getKey().equals("Skeleton")) {
                statsText += "\n" + entry.getKey() + ": " + entry.getValue() + "    ";
                System.out.println(entry.getKey() + ": " + entry.getValue()); //na chwile - testy
            }
        }
        return statsText;
    }


    //statystyki w kazdej turze
    public static void updateStats(World world, Label statsLabel) {
        updateSpeciesCount(world);

        String statsText = "---> Stan na mapie: <---\n";
        statsText += "\nLiczba zjedzonego jedzenia: " + world.totalEatenFood + "\nIlość zmarłych zwierząt: " + world.deadAnimalCounter + "\n";
        System.out.println(statsText);
        statsText += animalStatsText();

        statsLabel.setText(statsText);
    }

    //statystyki końcowe
    public static void showEndStats(World world, Label statsLabel, int finalTick) {
        String statsText = ">>> KONIEC SYMULACJI <<<" + (world.isSimulationEnded()?"\n           (brak miejsca)":"");
        statsText += "\n\nWykonane tury: " + finalTick + "/" + Main.noTicks + "\nLiczba zjedzonego jedzenia: " + world.totalEatenFood + "\nIlość zmarłych zwierząt: " + world.deadAnimalCounter + "\n";
        statsText += "\n---> Stan końcowy na mapie: <---";
        System.out.println("\n" + statsText);
        statsText += animalStatsText();

        statsLabel.setText(statsText);
    }
}

