package ocean;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;


/**
 * Handles the simulation execution in a separate thread.
 * Runs the simulation cycles, updates GUI, and manages speed adjustments.
 */
class SimulationThread extends Thread {
    //encapsulation
    private final Main mainInstance; //instancja main <-> przechowuje referencje do main bo potrzeba jej parametrów
    private final Label statsLabel;
    private final Slider speedSlider;
    private int actualTick = 0;


    public SimulationThread(Main mainInstance, Label statsLabel, Slider speedSlider) {
        this.mainInstance = mainInstance;
        this.statsLabel = statsLabel;
        this.speedSlider = speedSlider;
    }


    /**
     * Runs the simulation loop.
     * Executes simulation steps, updates GUI, and applies speed settings.
     */
    @Override
    public void run() {  //run() odpala się zawsze chyba w wątku po wywołaniu start()
        SimulationStatsManager.writeToFile("\na1_species,a1_id,action,a2_species,a2_id,addition\n"); //nagłówek do akcji w turze w logach
        while (actualTick < Main.noTicks && !mainInstance.world.isSimulationEnded()) { //wykonuje ticks (ile zadane) lub dopuki nie zakończy się symulacja
            try {
                int speed = (int) speedSlider.getValue();
                Thread.sleep(speed); //każdy tick trwa 500ms (do zmiany)
            } catch (InterruptedException e) {
                e.printStackTrace(); //obsługuje sytuację gdy wątek zostanie przerwany w trakcie
            }

            mainInstance.world.runSimulation(actualTick); //uruchomienie symulacji jednego "kroku"

            //update widoku
            Platform.runLater(() -> { //uruchomienie kodu na głównym wątku JavaFX
                //normalnie przyjmuje Runnable które jest do definiowania kodu który ma byc wywołany w wątku i która ma metodę run()
                mainInstance.updateGrid(); //wywołanie update
                SimulationStatsManager.updateStats(mainInstance.world, statsLabel, actualTick);  //wywołanie update statów liczby zwierzat
            });
            actualTick++;
        }

        //wyświetlenie statystyk końcowych
        Platform.runLater(() -> {
            SimulationStatsManager.showEndStats(mainInstance.world, statsLabel, actualTick);
        });
    }
}