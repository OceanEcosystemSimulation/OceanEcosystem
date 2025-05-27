package ocean;

import body.Animal;

import javafx.scene.control.Label;

import java.io.FileWriter;
import java.io.IOException;
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
            statsText += "\n" + entry.getKey() + ": " + entry.getValue() + "    ";
            SimulationStatsManager.writeToFile(entry.getKey() + ": " + entry.getValue()); //na chwile - testy
        }
        SimulationStatsManager.writeToFile("\n");
        return statsText;
    }


    //statystyki w kazdej turze
    public static void updateStats(World world, Label statsLabel) {
        updateSpeciesCount(world);

        String statsText = "\n---> Stan na mapie: <---\n";
        statsText += "\nLiczba zjedzonego jedzenia: " + world.totalEatenFood + "\nIlosc zmarlych zwierzat: " + world.deadAnimalCounter + "\n";
        SimulationStatsManager.writeToFile(statsText);
        statsText += animalStatsText();

        statsLabel.setText(statsText);
    }

    //statystyki końcowe
    public static void showEndStats(World world, Label statsLabel, int finalTick) {
        String statsText = ">>> KONIEC SYMULACJI <<<" + (world.isSimulationEnded()?"\n           (brak miejsca)":"");
        statsText += "\n\nWykonane tury: " + finalTick + "/" + Main.noTicks + "\nLiczba zjedzonego jedzenia: " + world.totalEatenFood + "\nIlość zmarłych zwierząt: " + world.deadAnimalCounter + "\n";
        statsText += "\n---> Stan końcowy na mapie: <---";
        SimulationStatsManager.writeToFile("\n" + statsText);
        statsText += animalStatsText();

        statsLabel.setText(statsText);
    }


    //zapis logów do pliku
    public static void writeToFile(String line) {
        try {
            FileWriter writer = new FileWriter("logi.csv", true); //true dopisuje na końcu a nie początku
            writer.write(line + "\n"); //każdą linijkę osobno
            writer.close();
        } catch (IOException e) {
            System.out.println("write to file error!!");
            e.printStackTrace();
        }
    }

}

