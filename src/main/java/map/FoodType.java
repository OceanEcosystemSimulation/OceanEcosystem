package map;

import javafx.scene.image.Image;

public enum FoodType {
    NONE(null),
    PLANKTON("/images/Plankton2.png"), // grafika planktonu, by mógł być wyświetlany w GUI
    ALGAE("/images/Alga.png"); // grafika algi, by mogła być wyświetlana w GUI

    private final Image FoodImage; // grafika jedzenia - przechowywanie

    // pętla eliminująca przypadek 'null' i dobierająca ścieżkę grafiki
    FoodType(String FoodPath) {
        if (FoodPath != null) { // sprawdzenie, czy to jedzenie
            this.FoodImage = new Image(getClass().getResource(FoodPath).toExternalForm()); // zaladowanie pliku
        }
        else {
            this.FoodImage = null;
        }
    }

    // getter
    public Image getFoodImage() {
        return FoodImage; // getter do grafiki
    }

}

