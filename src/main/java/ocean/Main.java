package ocean;

import body.Animal;
import body.WorldObject;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.scene.image.ImageView;
import javafx.geometry.Rectangle2D;

import map.*;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.List;


public class Main extends Application {
    private static int tileSize; //piksele
    static int width, height, noFood, noCoral, noAnimals, noTicks, seed; //parametry wejsciowe

    public World world; //deklaracja objektu world
    private Rectangle[][] tilesTab; //tablica kafelków
    private GridPane grid; //deklaracja grid

    public static void main(String[] args) {
        // parametry, zaciągane z pliku .csv
        Map<String, Integer> config = new HashMap<>(); //stwierdzilam ze hashmap bo tak to by musiały byc w konkretnej kolejności i wgl i jakby któregoś zabraklo to problem i wgl

        try{
            File file = new File("parameters.csv"); //tworzenie objektu file
            Scanner scanner = new Scanner(file); //odczywtywanie wartości z file
            while (scanner.hasNextLine()) { //sprawdza czy jest jessvze linia do odczytania
                String line = scanner.nextLine(); //pobiera ją
                String[] parts = line.split(","); //dzieli linie na 2 czesci (rozdziela to co jest ,)
                if (parts.length == 2) { //jesli sa 2 czesci - jest poprawnie odczytane i wgl zapisane
                    config.put(parts[0], Integer.parseInt(parts[1])); //konwersja na int
                }
            }
        } catch (Exception e) {
            System.out.println("File error");
            e.printStackTrace(); //wyswietla szczegóły błędu???? szczerze nie pamietam czy niezbędne
            return;
        }

        //przypisanei wartości do pól
        width = config.getOrDefault("width", 32);
        height = config.getOrDefault("height", 18);
        noFood = config.getOrDefault("noFood", 0);
        noCoral = config.getOrDefault("noCoral", 0);
        noAnimals = config.getOrDefault("noAnimals", 0);
        noTicks = config.getOrDefault("ticks", 0);
        seed = config.getOrDefault("seed", 0); //idk jak defaultowo się to bierze, co dać najlepiej - 0 będzie chyba zawsze taka sama

        World.random = new Random(seed);

        Screen screen = Screen.getPrimary(); //pobiera główny ekran
        Rectangle2D bounds = screen.getVisualBounds(); //pobiera wymiary ekranu bez paska zadań itp
        double windowWidth = bounds.getWidth() * 0.8; //szerokość okna aplikacji na X%
        double windowHeight = bounds.getHeight() * 0.8; //wysokość okna aplikacji na X%

        tileSize = (int) Math.ceil(Math.min( ((windowWidth-350)/width), ((windowHeight-100)/height))); //-100pkt około na label itp


        launch(args); //uruchamia JavaFX ???
    }



    @Override
    public void start(Stage primaryStage) {
        world = new World(width, height, noFood, noCoral, noAnimals);

        SimulationDisplayManager displayManager = new SimulationDisplayManager(width, height, tileSize, barierSettings);

        displayManager.setupGrid();
        displayManager.setupBarrierLayer();

        this.tilesTab = displayManager.getTilesTab();
        this.grid = displayManager.getGrid();

        updateGrid(); //update rzeczy ustawionych

        Scene scene = displayManager.createScene(); //wszystkie ustawienia elementów aplikacji - tworzenie sceny

        primaryStage.setWidth(tileSize * width + 250); //ustawia szerokość okna aplikacji - POPIEPRZONE WIEC UWAGA
        primaryStage.setHeight(tileSize * height + 110); //ustawia wysokość okna aplikacji
        primaryStage.setScene(scene); //przypisuje scene do Stage - ustawia główną zawartość okna - określa co ma byc wyświetlane
        primaryStage.setTitle("Ocean Ecosystem Simulation"); //tytuł

        primaryStage.show();

        //uruchamianie osobnego wątku symulacji (w tle by działało gładko????) który co X ms wykonuje nowy cykl i odświeża interfejs
        new SimulationThread(this, displayManager.getStatsLabel(), displayManager.getSpeedSlider()).start();
    }



    private static final Pane barierSettings = new Pane(); // klasa odpowiedzialna za pozycjonowanie

    public static Pane getOverlay() {
        return barierSettings;
    }



    //aktualizuje wyglad kafelków
    void updateGrid() {
        for (int x = 0; x < width; x++) { //przechodzi przez kazdy kafelek w siatce
            for (int y = 0; y < height; y++) {
                Rectangle rect = tilesTab[x][y]; //pobranie kafelka z tabicy kafelków
                rect.setStrokeWidth(1);
                rect.setFill(Color.TRANSPARENT); //reszta pól - woda
            }
        }

        // czyści wszystkie stare obrazki, czyli te ktore zostaną po poprzedniej turze - zeby sie nie nakladaly na stare
        List<Node> toRemove = new ArrayList<>();
        for (Node node : grid.getChildren()) {
            if (node instanceof ImageView) {
                toRemove.add(node);
            }
        }

        grid.getChildren().removeAll(toRemove);


        /* -------------------------------OTOCZENIE - ŚRODOWISKO------------------------------- */
        Image CoralReefImage = null;
        URL imageUrl = getClass().getResource("/images/CoralReef.png");
        if (imageUrl != null) { CoralReefImage = new Image(imageUrl.toExternalForm()); }
        else { System.out.println("CoralReef image not found!"); }


        for (Coord center : world.getCoralReefCenter()) {
            ImageView CoralImage = new ImageView(CoralReefImage);

            CoralImage.setPreserveRatio(true); // 3 x 3
            CoralImage.setFitHeight(tileSize * 3); // 3 kratki wysokości
            CoralImage.setFitWidth(tileSize * 3); // 3 kratki szerokości

            GridPane.setColumnIndex(CoralImage, center.getX() - 1);
            GridPane.setRowIndex(CoralImage, center.getY() - 1);
            GridPane.setColumnSpan(CoralImage, 3);
            GridPane.setRowSpan(CoralImage, 3);

            grid.getChildren().add(CoralImage);
        }


        /* -------------------------------GRAFIKI JEDZENIA------------------------------- */

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                Tile tile = world.getTile(new Coord(x, y)); // pobiera kafalek
                if (tile.hasFood()) { // sprawdza, czy znajduje sie na nim jedzenie
                    Image FoodImage = tile.foodType.getFoodImage(); // pobiera grafike z Enum FoodType
                    if (FoodImage != null) { // jesli to faktycznie jedzenie, to je ustawia
                        ImageView food = new ImageView(FoodImage);
                        food.setPreserveRatio(true); // proporcja
                        food.setFitHeight(tileSize);
                        food.setFitWidth(tileSize);

                        // ustawianie współrzędnych
                        GridPane.setColumnIndex(food, x);
                        GridPane.setRowIndex(food, y);
                        GridPane.setHalignment(food, javafx.geometry.HPos.CENTER);
                        GridPane.setValignment(food, javafx.geometry.VPos.CENTER);

                        grid.getChildren().add(food); // dodanie do GUI
                    }
                }
            }
        }

        /* -------------------------------GRAFIKI ZWIERZĄT------------------------------- */

        List<WorldObject> objectslListCopy = new ArrayList<>(world.getObjects()); //kopia bo wątek jednocześnie działa na tamtej liście
        for (WorldObject object : objectslListCopy) {
            if (object instanceof Animal animal && !animal.isAlive()) {continue;} // sprawdzenie czy są w ogóle żywe (dla pewności)

            // dopasowanie grafiki do zwierzęcia
            ImageView image = switch (object) {
                case allAnimals.Nemo nemo -> nemo.getImageView();
                case allAnimals.Shark shark -> shark.getImageView();
                case allAnimals.Egg egg -> egg.getImageView();
                case allAnimals.Orca orca -> orca.getImageView();
                case allAnimals.Skeleton skeleton -> skeleton.getImageView();
                case allAnimals.OceanicPuffer oceanicPuffer -> oceanicPuffer.getImageView();
                case allAnimals.Whale whale -> whale.getImageView();
                case allAnimals.Octopus octopus -> octopus.getImageView();
                case allAnimals.Dolphin dolphin -> dolphin.getImageView();
                case allAnimals.Seal seal -> seal.getImageView();
                case allAnimals.InkCloud ink -> ink.getImageView();
                case allAnimals.Turtle turtle -> turtle.getImageView();
                default -> null;
            };

            if (image != null) {
                image.setPreserveRatio(true);

                GridPane.setColumnIndex(image, object.getPosition().getX());
                GridPane.setRowIndex(image, object.getPosition().getY());

                GridPane.setHalignment(image, javafx.geometry.HPos.CENTER); //centering by nie były w lewym górnym
                GridPane.setValignment(image, javafx.geometry.VPos.CENTER);

                grid.getChildren().add(image);
            } else {  // jeśli nie ma grafiki, pojawi się czerwony kafelek
                Coord pos = object.getPosition();
                tilesTab[pos.getX()][pos.getY()].setFill(Color.RED);
            }
        }
    }


    public static int getTileSize() { return tileSize; }
}
