package ocean;

import body.Animal;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.image.ImageView;
import javafx.geometry.Rectangle2D;

import map.*;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.List;


public class Main extends Application {
    private static int tileSize; //piksele
    static int width, height, noFood, noCoral, noAnimals, noTicks; //parametry wejsciowe
    private final Map<String, Integer> speciesCount = new HashMap<>(); //tworzy HashMap: gatunek->ilość

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


        Screen screen = Screen.getPrimary(); //pobiera główny ekran
        Rectangle2D bounds = screen.getVisualBounds(); //pobiera wymiary ekranu bez paska zadań itp
        double windowWidth = bounds.getWidth() * 0.8; //szerokość okna aplikacji na X%
        double windowHeight = bounds.getHeight() * 0.8; //wysokość okna aplikacji na X%

        tileSize = (int) Math.ceil(Math.min( ((windowWidth-350)/width), ((windowHeight-100)/height))); //-100pkt około na label itp


        launch(args); //uruchamia JavaFX ???
    }

    @Override
    public void start(Stage primaryStage) {
        world = new World(width, height, noFood, noCoral, noAnimals, noTicks);

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

        updateGrid(); //update rzeczy ustawionych
        grid.setId("pane");

        VBox statsPanel = new VBox(); //tworzy taki kontener?? strukturę??? (VBox układa rzeczy jeden pod drugim)
        statsPanel.setTranslateY(5); //przesuwa statsPanel o 5px niżej
        statsPanel.setTranslateX(10); //przesuwa statsPanel o 10px w prawo
        statsPanel.setSpacing(50); //ustawienie odległości elementów od siebie
        Label statsLabel = new Label(); //tworzy label - takie do podstawowych tekstów (można zmienić na Text jeśli chcemy formatowania itp)
        statsPanel.getChildren().add(statsLabel); //dodaje statsLabel (element) do statsPanel

        Slider speedSlider = new Slider();
        speedSlider.setMin(100);
        speedSlider.setMax(2000);
        speedSlider.setValue(500); //wartość początkowa
        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);
        speedSlider.setSnapToTicks(true);
        speedSlider.setMajorTickUnit(100); //co ile się zmienia
        speedSlider.setMinorTickCount(0); //ilośc wartości pośrednich
        speedSlider.setPrefWidth(tileSize * width - 100); //ustawia szerokość slidera na szerokość mapy

        HBox bottomSection = new HBox();
        bottomSection.setTranslateY(8);
        bottomSection.setTranslateX(20);
        bottomSection.setSpacing(20);
        Label sliderLabel = new Label("Sleep time: ");
        sliderLabel.setTranslateY(5);
        bottomSection.getChildren().addAll(sliderLabel, speedSlider);

        VBox root = new VBox();
        HBox topSection = new HBox(grid, statsPanel);
        root.getChildren().addAll(topSection, bottomSection); //dodaje elementy siatka i panel do głównego jakby kontenera z elementami?? idk jak to się określa


        //tworzenie i wyswietlanie okna
        Scene scene = new Scene(root); //tworzy scene i dodaje root cały (wszystkie elementy)
        primaryStage.setWidth(tileSize * width + 250); //ustawia szerokość okna aplikacji - POPIEPRZONE WIEC UWAGA
        primaryStage.setHeight(tileSize * height + 110); //ustawia wysokość okna aplikacji
        primaryStage.setScene(scene); //przypisuje scene do Stage - ustawia główną zawartość okna - określa co ma byc wyświetlane
        primaryStage.setTitle("Ocean Ecosystem Simulation"); //tytuł

        URL cssUrl = getClass().getResource("/style.css");
        if (cssUrl != null) { scene.getStylesheets().add(cssUrl.toExternalForm()); }  //obsługa błędu jeśli nie ma style.css
        else { System.err.println("style.css not found!"); }

        primaryStage.show();

        //uruchamianie osobnego wątku symulacji (w tle by działało gładko????) który co X ms wykonuje nowy cykl i odświeża interfejs
        new SimulationThread(this, statsLabel, speedSlider).start();
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

        for (Animal animal : world.getAnimals()) {
            if (!animal.isAlive() && !(animal instanceof allAnimals.Skeleton)) continue; // sprawdzenie czy są w ogóle żywe (dla pewności)

            // dopasowanie grafiki do zwierzęcia
            ImageView image = switch (animal) {
                case allAnimals.Nemo nemo -> nemo.getImageView();
                case allAnimals.Shark shark -> shark.getImageView();
                case allAnimals.Egg egg -> egg.getImageView();
                case allAnimals.Orca orca -> orca.getImageView();
                case allAnimals.Skeleton skeleton -> skeleton.getImageView();
                case allAnimals.OceanicPuffer oceanicPuffer -> oceanicPuffer.getImageView();
                default -> null;
            };

            if (image != null) {
                image.setPreserveRatio(true);

                GridPane.setColumnIndex(image, animal.getPosition().getX());
                GridPane.setRowIndex(image, animal.getPosition().getY());

                GridPane.setHalignment(image, javafx.geometry.HPos.CENTER); //centering by nie były w lewym górnym
                GridPane.setValignment(image, javafx.geometry.VPos.CENTER);

                grid.getChildren().add(image);
            } else { // jeśli nie ma grafiki, pojawi się czerwony kafelek
                Coord pos = animal.getPosition();
                tilesTab[pos.getX()][pos.getY()].setFill(Color.RED);
            }
        }
    }

    //update statystyk w każdej turze
    void updateStats(Label statsLabel) {
        speciesCount.clear(); //czyści mapę - można jak będzie dużo rzeczy to zmienić to na jesli nie zywe to -1 i usuwa (zmienic z runSimulation) ale przy kilkuset podobno powinno byc git

        for (Animal animal : world.getAnimals()) {
            if (animal.isAlive()) { //sprawdza czy żywe
                speciesCount.put(animal.getName(), speciesCount.getOrDefault(animal.getName(), 0) + 1); //dodaje
                //pobiera nazwę, aktualną liczbę i powieksza o 1 - jesli nie ma jeszcze (deafult) to 0 (bo domysnie jest null wiec trzeba to tak
                //jak się nie podoba to getordefault to można ifem sprawdzac czy klucz istnieje containsKey i speciecCount.get...
            }
        }

        String statsText = "---> Stan na mapie: <---\n";
        statsText += "\nLiczba zjedzonego jedzenia: " + world.totalEatenFood + "\nIlość zmarłych zwierząt: " + world.deadAnimalCounter + "\n";
        System.out.println(statsText);
        for (Map.Entry<String, Integer> entry : speciesCount.entrySet()) {
            if (!entry.getKey().equals("Skeleton")) {
                statsText += "\n" + entry.getKey() + ": " + entry.getValue() + "    ";
                System.out.println(entry.getKey() + ": " + entry.getValue()); //na chwile - testy
            }
        }

        statsLabel.setText(statsText); //ustawianie nowego tekstu w Label
    }


    //statystyki końcowe
    void showEndStats(Label statsLabel, int finalTick) {
        String statsText = ">>> KONIEC SYMULACJI <<<" + (world.isSimulationEnded()?"\n           (brak miejsca)":"");
        statsText += "\n\nWykonane tury: " + finalTick + "/" + noTicks + "\nLiczba zjedzonego jedzenia: " + world.totalEatenFood + "\nIlość zmarłych zwierząt: " + world.deadAnimalCounter + "\n";
        statsText += "\n---> Stan końcowy na mapie: <---";
        System.out.println("\n" + statsText);
        for (Map.Entry<String, Integer> entry : speciesCount.entrySet()) {
            if (!entry.getKey().equals("Skeleton")) {
                statsText += "\n" + entry.getKey() + ": " + entry.getValue() + "    ";
                System.out.println(entry.getKey() + ": " + entry.getValue()); //na chwile - testy
            }
        }
        statsLabel.setText(statsText);
    }


    public static int getTileSize() { return tileSize; }
}
