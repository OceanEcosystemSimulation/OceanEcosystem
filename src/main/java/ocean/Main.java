package ocean;

import body.Animal;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
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
import java.util.*;
import java.util.List;


public class Main extends Application {
    private static int tileSize; //piksele
    static int width, height, noFood, noCoral, noAnimals, noTicks; //parametry wejsciowe
    private Map<String, Integer> speciesCount = new HashMap<>(); //tworzy HashMap: gatunek->ilość

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
        } catch (Exception e) { //idk czy dac Exeption czy FileNotFoundException ale na razie exception chyba bo przechwytuje wszystkie wyjatki nawet przy konwersji
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
        Rectangle2D bounds = screen.getVisualBounds(); //pobiera wymary ekranu bez paska zadań itp
        double windowWidth = bounds.getWidth() * 0.8; //szerokość okna aplikacji na X%
        double windowHeight = bounds.getHeight() * 0.8; //wysokość okna aplikacji na X%

        tileSize = (int) Math.min( (windowWidth/width), ((windowHeight-150)/height) ); //-100pkt około na label itp


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

        VBox bottomPanel = new VBox(); //tworzy taki kontener?? strukturę??? (VBox układa rzeczy jeden pod drugim)
        bottomPanel.setTranslateY(5); //przesuwa bottomPanel o 5px niżej
        bottomPanel.setTranslateX(10); //przesuwa bottomPanel o 10px w prawo
        bottomPanel.setSpacing(50); //ustawienie odległości elementów od siebie
        Label statsLabel = new Label(); //tworzy label - takie do podstawowych tekstów (można zmienić na Text jeśli chcemy formatowania itp)

        bottomPanel.getChildren().add(statsLabel); //dodaje statsLabel (element) do bottomPanel
        VBox root = new VBox(grid, bottomPanel); //dodaje elementy siatka i panel do głównego jakby kontenera z elementami?? idk jak to się określa
        grid.setId("pane");

        //tworzenie i wyswietlanie okna
        Scene scene = new Scene(root); //tworzy scene i dodaje root cały (wszystkie elementy)
        primaryStage.setWidth(tileSize * width); //ustawia szerokość okna aplikacji
        primaryStage.setHeight(tileSize * height + 110); //ustawia wysokość okna aplikacji
        primaryStage.setScene(scene); //przypisuje scene do Stage - ustawia główną zawartość okna - określa co ma byc wyświetlane
        primaryStage.setTitle("Ocean Ecosystem Simulation"); //tytuł
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.show();

        //uruchamianie osobnego wątku symulacji (w tle by działało gładko????) który co X ms wykonuje nowy cykl i odświeża interfejs
        new SimulationThread(this, statsLabel, primaryStage).start();
    }


    private boolean isImageView(Node change) {
        return change instanceof ImageView;
    }

    //aktualizuje wyglad kafelków
    void updateGrid() {
        for (int x = 0; x < width; x++) { //przechodzi przez kazdy kafelek w siatce
            for (int y = 0; y < height; y++) {
                Coord coord = new Coord(x, y);
                Tile tile = world.getTile(coord); //pobranie info o polu z world
                Rectangle rect = tilesTab[x][y]; //pobranie kafelka z tabicy kafelków

                //rect.setFill(Color.color(0, 0, 0, 0)); //reszta pól - woda
                rect.setFill(Color.TRANSPARENT); //reszta pól - woda
            }
        }

        // czyści wszystkie stare obrazki, czyli te ktore zostaną po poprzedniej turze
        // zeby sie nie nakladaly na stare
        List<Node> toRemove = new ArrayList<>();
        for (Node node : grid.getChildren()) {
            if (node instanceof ImageView) {
                toRemove.add(node);
            }
        }

        grid.getChildren().removeAll(toRemove);

        /* -------------------------------OTOCZENIE - ŚRODOWISKO------------------------------- */
        Image CoralReefImage = new Image(getClass().getResource("/images/CoralReef.png").toExternalForm());

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
                        GridPane.setRowIndex(food,  y);
                        GridPane.setHalignment(food, javafx.geometry.HPos.CENTER);
                        GridPane.setValignment(food, javafx.geometry.VPos.CENTER);

                        grid.getChildren().add(food); // dodanie do GUI
                    }
                }
            }
        }

        /* -------------------------------GRAFIKI ZWIERZĄT------------------------------- */

        for (Animal animal : world.getAnimals()) {
            if (!animal.isAlive()) continue; // sprawdzenie czy są w ogóle żywe (dla pewności)
            //TODO: dodać grafikę dla martwych (Skeleton)

            // dopasowanie grafiki do zwierzęcia
            ImageView image = switch (animal) { //intellij stwierdził że lepiej dać switch case + będzei to czytelniejsze raczej
                case allAnimals.Nemo nemo -> nemo.getImageView();
                case allAnimals.Shark shark -> shark.getImageView();
                case allAnimals.Egg egg -> egg.getImageView();
                case allAnimals.Orca orca -> orca.getImageView();
                default -> null;
            };

            if (image != null) {
                image.setPreserveRatio(true);

                GridPane.setColumnIndex(image, animal.getPosition().getX());
                GridPane.setRowIndex(image, animal.getPosition().getY());

                GridPane.setHalignment(image, javafx.geometry.HPos.CENTER); //centering by nie były w lewym górnym
                GridPane.setValignment(image, javafx.geometry.VPos.CENTER);

                grid.getChildren().add(image);
            } else {
                // jeśli nie ma grafiki, pojawi się czerwony kafelek
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

        String statsText = "---> Aktualna liczba gatunkow na mapie: <---";
        System.out.println("\n" + statsText);
        for (Map.Entry<String, Integer> entry : speciesCount.entrySet()) {
            statsText += "\n" + entry.getKey() + ": " + entry.getValue() + "    ";
            System.out.println(entry.getKey() + ": " + entry.getValue()); //na chwile - testy
        }

        statsLabel.setText(statsText); //ustawianie nowego tekstu w Label
    }


    //statystyki końcowe
    void showEndStats(Label statsLabel, Stage stage, int finalTick) {
        String statsText = ">>> KONIEC SYMULACJI " + (world.isSimulationEnded()?" (z powodu braku miejsca) ":"") + "<<<\n" + "\nWykonane tury: " + finalTick + "/" + noTicks;
        statsText += "\nLiczba zjedzonego jedzenia: " + world.totalEatenFood + "\nUmarlych: " + world.deadAnimalCounter + "\n";
        statsText += "\n---> Stan końcowy na mapie: <---";
        System.out.println("\n" + statsText);
        for (Map.Entry<String, Integer> entry : speciesCount.entrySet()) {
            statsText += "\n" + entry.getKey() + ": " + entry.getValue() + "    ";
            System.out.println(entry.getKey() + ": " + entry.getValue()); //na chwile - testy
        }
        statsLabel.setText(statsText);

        double newHeight = stage.getHeight() + 110; //dodaje Xpt do dołu by pokazywało staty całe
        stage.setHeight(newHeight);
    }


    public static int getTileSize() { return tileSize; }
}
