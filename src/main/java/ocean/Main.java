package ocean;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.*;

public class Main extends Application {
    private static final int tileSize = 25; //piksele

    static int width = 20;
    static int height = 20;
    static int noFood = 10;
    static int noCoral = 5;
    static int noAnimals;
    static int ticks;

    public World world; //deklaracja objektu world
    private Rectangle[][] tilesTab; //tablica kafelków

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Podaj liczbę zwierząt:");
        noAnimals = scanner.nextInt();

        System.out.println("Podaj liczbę cykli symulacji:");
        ticks = scanner.nextInt();

        //itd wszystkie parametry idkkk

        launch(args); //uruchamia JavaFX ???
    }

    @Override
    public void start(Stage primaryStage) {
        world = new World(width, height, noFood, noCoral, noAnimals, ticks);

        //GridPane pozwala na organizację elementów w formie siatki a nie jakiś węzłów więc to wzięłam ale nwm szczerze co robię XD
        GridPane grid = new GridPane(); //tworzenie układu siatki na której będą wyświetlane kafelki
        tilesTab = new Rectangle[width][height]; //tablica kafelków update wielkości

        //tworzenie kafelków i dodawanie do GridPane
        for (int x = 0; x < width; x++) { //przechodzi po kolei width x height
            for (int y = 0; y < height; y++) {
                Rectangle rectangle = new Rectangle(tileSize, tileSize); //tworzy kafelek (rectancle)
                rectangle.setStroke(Color.GRAY); //robi go szarym na obwodzie (na razie)
                tilesTab[x][y] = rectangle; //dodaje do tablicy by móc później nim zarzadzac
                grid.add(rectangle, x, y); //dodaje obiekt rectangle do siatki na współrzędne xy
            }
        }

        updateGrid(); //update rzeczy ustawionych

        //tworzenie i wyswietlanie okna
        Scene scene = new Scene(grid); //tworzy scene
        primaryStage.setScene(scene); //dodaje objekty Stage do scene
        primaryStage.setTitle("Ocean Ecosystem Simulation"); //tytuł
        primaryStage.show();

        //uruchamianie osobnego wątku symulacji (w tle by działało gładko????) który co X ms wykonuje nowy cykl i odświeża interfejs
        //szerze nwm jak to działa za bardzo, wzięłam to z jakiegoś blogu
        new SimulationThread(this).start();
    }


    //aktualizuje wyglad kafelków
    void updateGrid() {
        for (int x = 0; x < width; x++) { //przechodzi przez kazdy kafelek w siatce
            for (int y = 0; y < height; y++) {
                Coord coord = new Coord(x, y);
                Tile tile = world.getTile(coord); //pobranie info o polu z world
                Rectangle rect = tilesTab[x][y]; //pobranie kafelka z tabicy kafelków

                if (tile.type == MapType.CORAL) { //pole to coral
                    rect.setFill(Color.DARKCYAN);
                } else {
                    rect.setFill(Color.LIGHTBLUE); //reszta pól - woda
                }

                if (world.isOccupied(coord)) { //zajety przez zwierze (do zmiany bo na razie wszystkie takie same)
                    rect.setFill(Color.RED);
                } else if (tile.hasFood()) { //zajety przez jedzenie
                    rect.setFill(Color.GREEN);
                }
            }
        }
    }


    //wyswietlanie statystyk -- jeśli nie są potrzebne w różnych momentach i tylko na końcu to można przenieść do SimulationThread
    void showAnimalStats() {
        Map<String, Integer> speciesCount = new HashMap<>(); //tworzy HashMap: gatunek->ilość

        for (Animal animal : world.getAnimals()) { //przejscie po zwierzetach wszystkich
            if (animal.isAlive()) { //sprawdza czy żywe
                speciesCount.put(animal.getName(), speciesCount.getOrDefault(animal.getName(), 0) + 1); //dodaje
                //pobiera nazwę, aktualną liczbę i powieksza o 1 - jesli nie ma jeszcze (deafult) to 0 (bo domysnie jest null wiec trzeba to tak
                //jak się nie podoba to getordefault to można ifem sprawdzac czy klucz istnieje containsKey i speciecCount.get...
            }
        }

        System.out.println("\n---> Stan koncowy symulacji: <---");
        for (Map.Entry<String, Integer> entry : speciesCount.entrySet()) { //przechodzi po wszystkich dodanych w hashmap
            System.out.println(entry.getKey() + ": " + entry.getValue()); //bierze klucz i wartość
        }
    }
}
