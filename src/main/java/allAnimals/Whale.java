package allAnimals;

import javafx.application.Platform;
import extendedMechanics.Reproduction;
import map.Coord;
import map.Tile;
import ocean.*;
import body.Animal;
import body.Genes;
import body.Herbivorous;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Whale extends Herbivorous {
    public Whale(Coord position) {
        super(position, generateGenes());
        setName("Whale");
        settings();
    }

    //konstruktor dziecka
    public Whale(Coord position, Animal parent1, Animal parent2) {
        super(position, parent1, parent2);
        setName("Whale");
        settings();
    }


    /* -------------------------------GENY------------------------------- */

    //do tworzenia genów w nowych - zakresy w losowych wartościah do zmiany
    //nie może potrzebować objektu by dzialac bo to ma tworzyć konstruktor (objekt) a nie byc uzywanym przez niego wiec static
    private static Genes generateGenes() {
        Genes genes = new Genes();
        genes.setStrength(5);
        genes.setSpeed(2);
        genes.setMaxAge(100 + World.random.nextInt(50));
        genes.setMaxLoneliness(40 + World.random.nextInt(20));
        genes.setMaxEnergy(80);
        return genes;
    }


    /* -------------------------------LIFE------------------------------- */

    public void update(World world) {
        processLifeCycle(world); //duperele o życiu
        updateWhaleGraphics();
        if (!isAlive()) return;

        Reproduction.pregnancyTick(world, this);

        tryToEat(world, this); //wywołanie mechaniki jedzenia
        tryToMate(world); //wywołanie mechaniki rozmnażania
        move(world); //wywołanie mechaniki ruchu
        barrier(world);
    }

    @Override
    public void die(World world) {
        super.die(world);
        barrierLayer.setVisible(false);
        // Platform.runLater przekazuje wyświetlenie bariery do GUI, by uniknąć błędu java.lang.IllegalStateException: Not on FX application thread; currentThread = Thread-3
        Platform.runLater(new Runnable() { // bez Platform.runLater pojawia się błąd Thead3
            @Override
            public void run() {
                Main.getOverlay().getChildren().add(barrierLayer); // dodanie grafiki bąbla
            }
        });
    }

    @Override
    public Animal giveBirth(Coord position, Animal parent1, Animal parent2) {
        return new Whale(position, parent1, parent2);
    }


    /* -------------------------------MECHANIKA JEDZENIA------------------------------- */

    //zjada o ile nie byłoby ponad 100 napchane
    @Override
    public void eat(Tile tile, World world) { //przykładowe jak pisać
        int gain = switch (tile.foodType) {
            case PLANKTON -> 10;
            case ALGAE -> 15;
            default -> 0; //NONE
        };
        if (getFoodLevel()+gain <= 100){
            setFoodLevel(getFoodLevel() + gain); //je
            System.out.println(this.getName() + " id: " + this.getId() + " eats " + tile.foodType);
            tile.clearFood(world);
        }
    }

    /* -------------------------------BARIERA - SUPERUMIEJĘTNOŚĆ------------------------------- */

    private final List<Coord> barrierCoords = new ArrayList<>(); // przechowuje koordynaty bariery (miejsca, w których widać barierę)
    private static final int barrierRange = 1; // promień - 1 kratka od wieloryba w każdą stonę, w tym na skos

    private void barrier(World world) {
        for (Coord coord : barrierCoords) {
            if (world.coordsInBounds(coord)) {
                world.getTile(coord).disactiveBarrier(); // usuwa efekt bariery, dekrementując licznik
            }
        }
        barrierCoords.clear(); // co nową turę trzeba wyczyścić, zmieniają się koordynaty

        for (int x = -barrierRange; x <= barrierRange; x++) {
            for (int y = -barrierRange; y <= barrierRange; y++) {
                if (x == 0 && y == 0) { // pominięcie kafelka, na którym stoi wieloryb
                    continue;
                }

                Coord position = getPosition().shifted_coordinate(x, y); //sprawdzenie czy mieści się w graniach mapy
                if (world.coordsInBounds(position)) {
                    world.getTile(position).activeBarrier(); // inkrementuje licznik, po pojawieniu się bariery
                    barrierCoords.add(position); // zapisuje pozycje, by zdezaktywować po ticku
                }
            }
        }
        // ogólnie działa, ale //TODO: wyśrodkowanie w tym miejscu
    }

    /* -------------------------------GRAFIKI------------------------------- */

    private static Image youngWhale;
    private static Image oldWhale;
    private static Image barrierImage;
    private static final int AGE_OLD = 18; // one turn = one month

    private final ImageView imageView = new ImageView();
    public ImageView getImageView() { return imageView; } // getter
    private final ImageView barrierLayer = new ImageView(barrierImage);


    /* -------------------------------GUI------------------------------- */


    private static void loadImagesIfNeeded() {
        if (youngWhale == null || oldWhale == null || barrierImage == null) {
            if (!GraphicsEnvironment.isHeadless()) { //sprawdza czy jest srodowisko graficzne (testy go nie mają)
                youngWhale = new Image(Objects.requireNonNull(Whale.class.getResource("/images/BabyWhale.png")).toExternalForm());
                oldWhale = new Image(Objects.requireNonNull(Whale.class.getResource("/images/Whale.png")).toExternalForm());
                barrierImage = new Image(Objects.requireNonNull(Whale.class.getResource("/images/barrier.png")).toExternalForm());
            }
        }
    }

    private void settings() {
        imageView.setPreserveRatio(true);
        barrierLayer.setVisible(false); // początkowo warstwa - bariera niewidoczna
        updateWhaleGraphics();
        barrierLayer.setOpacity(0.5);

        Platform.runLater(new Runnable() { // bez Platform.runLater pojawia się błąd Thead3
            @Override
            public void run() {
                Main.getOverlay().getChildren().add(barrierLayer); // dodanie grafiki bąbla
            }
        });
    }

    private void updateWhaleGraphics() {
        loadImagesIfNeeded();

        boolean isYoung = getAge() < AGE_OLD;
        imageView.setImage(isYoung ? youngWhale : oldWhale);

        double scale = isYoung ? 0.7 : 1.0;
        imageView.setFitWidth(Main.getTileSize() * scale);
        imageView.setFitHeight(Main.getTileSize() * scale);
    }
}