package ocean;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.net.URL;

public class SimulationDisplayManager {
    private final int width, height, tileSize;
    private GridPane grid;
    private Rectangle[][] tilesTab;
    private VBox statsPanel;
    private Slider speedSlider;
    private StackPane barierSettings;

    public SimulationDisplayManager(int width, int height, int tileSize, Pane barierSettings) {
        this.width = width;
        this.height = height;
        this.tileSize = tileSize;
        this.barierSettings = new StackPane(barierSettings);
    }


    //ustawienia siatki
    void setupGrid() {
        //GridPane pozwala na organizację elementów w formie siatki a nie jakiś węzłów więc to wzięłam ale nwm szczerze co robię XD
        grid = new GridPane(); //tworzenie układu siatki na której będą wyświetlane kafelki
        tilesTab = new Rectangle[width][height]; //tablica kafelków update wielkości

        //tworzenie kafelków i dodawanie do GridPane
        for (int x = 0; x < width; x++) { //przechodzi po kolei width x height
            for (int y = 0; y < height; y++) {
                Rectangle rectangle = new Rectangle(tileSize, tileSize); //tworzy kafelek (rectancle)
                rectangle.setStroke(Color.GRAY); //robi go szarym na obwodzie (na razie) //TRANSPARENT - jak któraś chce bez gridu
                tilesTab[x][y] = rectangle; //dodaje do tablicy by móc później nim zarzadzac
                grid.add(rectangle, x, y); //dodaje obiekt rectangle do siatki na współrzędne xy
            }
        }

        grid.setId("pane"); //ustawia id dla grid - używane w css lub odwołania
    }


    //ustawienia panelu ze statami
    private VBox setupStatsPanel() {
        statsPanel = new VBox(); //tworzy taki kontener?? strukturę??? (VBox układa rzeczy jeden pod drugim)
        statsPanel.setTranslateY(5); //przesuwa statsPanel o 5px niżej
        statsPanel.setTranslateX(10); //przesuwa statsPanel o 10px w prawo
        statsPanel.setSpacing(50); //ustawienie odległości elementów od siebie

        Label statsLabel = new Label(); //tworzy label - takie do podstawowych tekstów (można zmienić na Text jeśli chcemy formatowania itp)
        statsPanel.getChildren().add(statsLabel); //dodaje statsLabel (element) do statsPanel

        return statsPanel;
    }


    //ustawienia slidera
    private Slider setupSpeedSlider() {
        speedSlider = new Slider();
        speedSlider.setMin(100);
        speedSlider.setMax(2000);
        speedSlider.setValue(500); //wartość początkowa
        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);
        speedSlider.setSnapToTicks(true);
        speedSlider.setMajorTickUnit(100); //co ile się zmienia
        speedSlider.setMinorTickCount(0); //ilośc wartości pośrednich
        speedSlider.setPrefWidth(tileSize * width - 100); //ustawia szerokość slidera na szerokość mapy

        return speedSlider;
    }


    //ustawienia bariery
    void setupBarrierLayer() {
        barierSettings = new StackPane();
        barierSettings.setPrefSize(width * tileSize, height * tileSize);
        barierSettings.setClip(new Rectangle(width * tileSize, height * tileSize));
        barierSettings.setMouseTransparent(true);
    }


    //ustawienia tła
    private void applyBackgroundStyles(Scene scene) {
        URL cssUrl = getClass().getResource("/style.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.out.println("style.css not found!");
        }
    }


    //tworzenie sceny
    public Scene createScene() {
        VBox statsPanel = setupStatsPanel();
        Slider speedSlider = setupSpeedSlider();

        StackPane layouts = new StackPane(grid, barierSettings); // stos, układa elementy warstwami 0 - warstwa na dole - grid, 1 warstwa wyżej - bariera

        Label sliderLabel = new Label("Sleep time: ");
        sliderLabel.setTranslateY(5);

        HBox bottomSection = new HBox();
        bottomSection.setTranslateY(8);
        bottomSection.setTranslateX(20);
        bottomSection.setSpacing(20);
        bottomSection.getChildren().addAll(sliderLabel, speedSlider);

        HBox topSection = new HBox(layouts, statsPanel);
        VBox root = new VBox();
        root.getChildren().addAll(topSection, bottomSection); //dodaje elementy siatka i panel do głównego jakby kontenera z elementami?? idk jak to się określa

        Scene scene = new Scene(root); //tworzy scene i dodaje root cały (wszystkie elementy)
        applyBackgroundStyles(scene);

        return scene;
    }


    public GridPane getGrid() { return grid; }
    public Rectangle[][] getTilesTab() { return tilesTab; }
    public Slider getSpeedSlider() { return speedSlider; }
    public Label getStatsLabel() { return (Label) statsPanel.getChildren().get(0); }  //rzutowanie na Label bo normalnie zwraca Node
}
