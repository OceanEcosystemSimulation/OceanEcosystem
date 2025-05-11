package ocean;

import javafx.application.Platform;

class SimulationThread extends Thread {
    private Main mainInstance; //instancja main <-> przechowuje referencje do main bo potrzeba jej parametrów

    // szczerze już nwm co robię, teoretycznie przekazuje referencję do obiektu main żeby wątek miał dostęp do metod i danych
    public SimulationThread(Main mainInstance) {
        this.mainInstance = mainInstance;
    }


    //odpowiada za przeprowadzenie symulacji
    @Override
    public void run() {  //run() odpala się zawsze chyba w wątku po wywołaniu start()
        for (int i = 0; i < Main.ticks; i++) { //wykonuje ticks (ile zadane)
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
            });
        }

        //wyświetlenie statystyk
        Platform.runLater(() -> {
            mainInstance.showAnimalStats(); //wywołanie statystyk
        });
    }
}