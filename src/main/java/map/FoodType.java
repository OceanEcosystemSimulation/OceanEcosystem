package map;

import javafx.scene.image.Image;

import java.awt.*;
import java.net.URL;

public enum FoodType {
    NONE(null),
    PLANKTON("/images/Plankton.png"), // grafika planktonu, by mógł być wyświetlany w GUI
    ALGAE("/images/Alga.png"); // grafika algi, by mogła być wyświetlana w GUI

    //encapsulation
    private final Image FoodImage; // grafika jedzenia - przechowywanie

    // pętla eliminująca przypadek 'null' i dobierająca ścieżkę grafiki
    FoodType(String FoodPath) {
        if (FoodPath == null || GraphicsEnvironment.isHeadless()) {
            FoodImage = null; //nie ładuje gdy nie może
        } else {
            URL foodImageUrl = getClass().getResource(FoodPath);
            if (foodImageUrl != null) { //sprawdzenie czy na pewno istnieje plik
                this.FoodImage = new Image(foodImageUrl.toExternalForm()); //załadowanie go
            } else {
                System.out.println("food image not found!: " + FoodPath); //pokaze tez który nie zaladowało
                this.FoodImage = null;
            }
        }
    }


    public Image getFoodImage() {
        return FoodImage;
    }
}

