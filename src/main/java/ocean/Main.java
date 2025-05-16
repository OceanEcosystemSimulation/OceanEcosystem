package ocean;

import body.Animal;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.image.ImageView;

import map.*;

import java.io.File;
import java.util.*;

public class Main extends Application {
    private static final int tileSize = 30; //piksele
    static int width, height, noFood, noCoral, noAnimals, ticks; //parametry wejsciowe
    private Map<String, Integer> speciesCount = new HashMap<>(); //tworzy HashMap: gatunek->ilość

    public World world; //deklaracja objektu world
    private Rectangle[][] tilesTab; //tablica kafelków
    private GridPane grid; //deklaracja grid

    public static void main(String[] args) {
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
        ticks = config.getOrDefault("ticks", 0);

        launch(args); //uruchamia JavaFX ???
    }

    @Override
    public void start(Stage primaryStage) {
        world = new World(width, height, noFood, noCoral, noAnimals, ticks);

        //GridPane pozwala na organizację elementów w formie siatki a nie jakiś węzłów więc to wzięłam ale nwm szczerze co robię XD
        grid = new GridPane(); // tworzenie układu siatki na której będą wyświetlane kafelki // zapisuje do pola klasy
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
        bottomPanel.setSpacing(20); //ustawia odległość między elementami
        Label statsLabel = new Label(); //tworzy label - takie do podstawowych tekstów (można zmienić na Text jeśli chcemy formatowania itp)


        bottomPanel.getChildren().add(statsLabel); //dodaje statsLabel (element) do bottomPanel
        VBox root = new VBox(grid, bottomPanel); //dodaje elementy siatka i panel do głównego jakby kontenera z elementami?? idk jak to się określa
        root.setId("pane");


        //tworzenie i wyswietlanie okna
        Scene scene = new Scene(root); //tworzy scene i dodaje root cały (wszystkie elementy)
        primaryStage.setScene(scene); //przypisuje scene do Stage - ustawia główną zawartość okna - określa co ma byc wyświetlane
        primaryStage.setTitle("Ocean Ecosystem Simulation"); //tytuł
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.show();

        //uruchamianie osobnego wątku symulacji (w tle by działało gładko????) który co X ms wykonuje nowy cykl i odświeża interfejs
        //szerze nwm jak to działa za bardzo, wzięłam to z jakiegoś blogu
        new SimulationThread(this, statsLabel).start();
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

                if (tile.getMapType() == MapType.CORAL) { //pole to coral
                    rect.setFill(Color.DARKCYAN);
                } else {
                    rect.setFill(Color.color(0, 0,0, 0)); //reszta pól - woda
                }

                /*if (world.isOccupied(coord)) { //zajety przez zwierze (do zmiany bo na razie wszystkie takie same)
                    rect.setFill(Color.RED);
                } else */ if (tile.hasFood()) { //zajety przez jedzenie
                    rect.setFill(Color.GREEN);
                }
            }

            List<Node> toRemove = new ArrayList<>();
            for (Node node : grid.getChildren()) {
                if (node instanceof ImageView) {
                    toRemove.add(node);
                }
            }
            grid.getChildren().removeAll(toRemove);

            for (Animal animal : world.getAnimals()) {
                if (!animal.isAlive()) continue;

                ImageView image = null;

                if (animal instanceof allAnimals.Nemo nemo) {
                    image = nemo.getImageView();
                } else if (animal instanceof allAnimals.Shark shark) {
                    image = shark.getImageView();
                } else if (animal instanceof allAnimals.Egg egg) {
                    image = egg.getImageView();
                }

                if (image != null) {
                    image.setFitWidth(tileSize);
                    image.setFitHeight(tileSize);
                    image.setPreserveRatio(true);

                    GridPane.setColumnIndex(image, animal.getPosition().getX());
                    GridPane.setRowIndex(image, animal.getPosition().getY());
                    grid.getChildren().add(image);
                } else {
                    Coord pos = animal.getPosition();
                    tilesTab[pos.getX()][pos.getY()].setFill(Color.RED);
                }
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

        String statsText = "---> Aktualna liczba gatunków na mapie: <---\n";
        System.out.println();
        for (Map.Entry<String, Integer> entry : speciesCount.entrySet()) {
            statsText += entry.getKey() + ": " + entry.getValue() + "    ";
            System.out.println(entry.getKey() + ": " + entry.getValue()); //na chwile - testy
        }

        statsLabel.setText(statsText); //ustawianie nowego tekstu w Label
    }

    //wyswietlanie statystyk końcowych - idk czy bedzie potrzebne jak mamy te updateStats ale na razie zostawie
    void showAnimalStats() {
        System.out.println("\n---> Stan koncowy symulacji: <---");
        for (Map.Entry<String, Integer> entry : speciesCount.entrySet()) { //przechodzi po wszystkich dodanych w hashmap
            System.out.println(entry.getKey() + ": " + entry.getValue()); //bierze klucz i wartość
        }
    }


    public static int getTileSize() { return tileSize; }
}
