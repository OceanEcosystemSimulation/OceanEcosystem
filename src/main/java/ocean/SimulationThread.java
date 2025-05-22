package ocean;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.stage.Stage;

class SimulationThread extends Thread {
    private Main mainInstance; //instancja main <-> przechowuje referencje do main bo potrzeba jej parametrów
    private Label statsLabel;
    private Stage primaryStage;

    // szczerze już nwm co robię, teoretycznie przekazuje referencję do obiektu main żeby wątek miał dostęp do metod i danych
    public SimulationThread(Main mainInstance, Label statsLabel, Stage primaryStage) {
        this.mainInstance = mainInstance;
        this.statsLabel = statsLabel;
        this.primaryStage = primaryStage;
    }


    //odpowiada za przeprowadzenie symulacji
    @Override
    public void run() {  //run() odpala się zawsze chyba w wątku po wywołaniu start()
        int tick = 0;
        while (tick < Main.noTicks && !mainInstance.world.isSimulationEnded()) { //wykonuje ticks (ile zadane) lub dopuki nie zakończy się symulacja
            try {
                Thread.sleep(500); //każdy tick trwa 500ms (do zmiany)
            } catch (InterruptedException e) {
                e.printStackTrace(); //obsługuje sytuację gdy wątek zostanie przerwany w trakcie
            }

            mainInstance.world.runSimulation(1); //uruchomienie symulacji jednego "kroku"

            //update widoku
            Platform.runLater(() -> { //uruchomienie kodu na głównym wątku JavaFX
                //normalnie przyjmuje Runnable które jest do definiowania kodu który ma byc wywołany w wątku i która ma metodę run()
                mainInstance.updateGrid(); //wywołanie update
                mainInstance.updateStats(statsLabel); //wywołanie update statów liczby zwierzat
            });
            tick++;
        }

        int finalTick = tick;

        //wyświetlenie statystyk końcowych
        Platform.runLater(() -> {
            mainInstance.showEndStats(statsLabel, primaryStage, finalTick);
        });
    }
}