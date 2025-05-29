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
        String speciesNames = "\n";
        String noAnimals = "";
        for (Map.Entry<String, Integer> entry : speciesCount.entrySet()) {
            statsText += "\n" + entry.getKey() + ": " + entry.getValue() + "    ";
            speciesNames += entry.getKey() + ", ";
            noAnimals += entry.getValue() + ", ";
        }
        writeToFile(speciesNames + "\n" + noAnimals + "\n");
        return statsText;
    }


    //statystyki w kazdej turze
    public static void updateStats(World world, Label statsLabel, int actualTick) {
        updateSpeciesCount(world);

        String statsText = "\n---> Stan na mapie: <---\n";
        statsText += "\nLiczba zjedzonego jedzenia: " + world.totalEatenFood + "\nIlosc zmarlych zwierzat: " + world.deadAnimalCounter + "\n";
        statsText += animalStatsText();
        statsLabel.setText(statsText);

        writeToFile("\nround,max_no_rounds,eaten_food,no_dead\n" + actualTick + "," + Main.noTicks + "," + world.totalEatenFood + "," + world.deadAnimalCounter + "\n"); //logi z stanu na mapie
        writeToFile("\na1_species,a1_id,action,a2_species,a2_id,addition\n"); //nagłówek do akcji w turze w logach
    }

    //statystyki końcowe
    public static void showEndStats(World world, Label statsLabel, int finalTick) {
        String statsText = ">>> KONIEC SYMULACJI <<<" + (world.isSimulationEnded()?"\n           (brak miejsca)":"");
        statsText += "\n\nWykonane tury: " + finalTick + "/" + Main.noTicks + "\nLiczba zjedzonego jedzenia: " + world.totalEatenFood + "\nIlość zmarłych zwierząt: " + world.deadAnimalCounter + "\n";
        writeToFile("\nno_rounds,max_no_rounds,eaten_food,no_dead\n" + finalTick + "," + Main.noTicks + "," + world.totalEatenFood + "," + world.deadAnimalCounter + "\n");

        statsText += "\n---> Stan końcowy na mapie: <---";
        statsText += animalStatsText();
        statsLabel.setText(statsText);
    }


    //zapis logów do pliku
    public static void writeToFile(String line) {
        try {
            FileWriter writer = new FileWriter("logi.csv", true); //true dopisuje na końcu a nie początku
            writer.write(line);
            writer.close();
        } catch (IOException e) {
            System.out.println("write to file error!!");
            e.printStackTrace();
        }
    }



    /*   >> ŚCIĄGA DO PRINTÓW <<
    a1_species,a1_id,action,a2_species,a2_id,addition

    SimulationStatsManager.writeToFile(this.getName() + "," + this.getId() + ",eats,,," + tile.foodType + "\n");
    SimulationStatsManager.writeToFile(this.getName() + "," + this.getId() + ",jumped to,,,[" + this.getPosition().x + ";" + this.getPosition().y + "]\n");
    SimulationStatsManager.writeToFile(female.getName() + "," + female.getId() + ",get pregnant with," + male.getName() + "," + male.getId() + "\n");
    SimulationStatsManager.writeToFile(prey.getName() + "," + prey.getId() + ",escape from," + this.getName() + "," + this.getId() + "\n");
    SimulationStatsManager.writeToFile("notification,Successfully added only" + maxNoFood + " from " + noFood + " food\n");
    SimulationStatsManager.writeToFile(this.getName() + "," + this.getId() + ",killed," + prey.getName() + "," + prey.getId() + "\n");

     */
}

