package allAnimals;

import body.*;
import map.*;
import ocean.Main;
import ocean.World;
import extendedMechanics.Reproduction;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import ocean.WorldSearch;

import java.awt.*;
import java.util.List;
import java.util.Objects;

import static body.AnimalCombatUtils.randomMove;


public class Orca extends Carnivorous {

    public Orca(Coord position) {
        super(position, generateGenes());
        setName("Orca");
        settings();
    }

    public Orca(Coord position, Animal parent1, Animal parent2) {
        super(position, parent1, parent2);
        setName("Orca");
        settings();
    }

    private static Genes generateGenes() {
        Genes g = new Genes();
        g.setStrength(70 + rand.nextInt(11));
        g.setSpeed(70 + rand.nextInt(11));
        g.setMaxAge(80 + rand.nextInt(20));
        g.setMaxLoneliness(50 + rand.nextInt(10));
        g.setMaxEnergy(100);
        return g;
    }


    @Override
    public void update(World world) {
        processLifeCycle(world);

        updateOrcaGraphics();

        if (!isAlive()) return;

        Reproduction.pregnancyTick(world, this);

        tryToMate(world);
        move(world);
        tryToAttack(world, this);
    }


    @Override
    public Animal giveBirth(Coord position, Animal parent1, Animal parent2) {
        return new Orca(position, parent1, parent2);
    }



    private static final List<String> preyList = List.of("Nemo", "Shark");


    @Override
    public boolean canAttack(Animal other) {
        return other != null && preyList.contains(other.getName());
    }


    @Override
    public int calculateGain(Animal animal) {
        return switch (animal.getName()) {
            case "Nemo" -> 30;
            case "Shark" -> 40;
            default -> 0;
        };
    }


    @Override
    public void move(World world) {
        int speed = isFoodNearby(world) ? getGenes().getSpeed() + 10 : getGenes().getSpeed();
        System.out.println(getName() + " [" + getId() + "] is chasing food");

        if (getFoodLevel() < 70) {
            Coord preyPos = WorldSearch.nearestPrey(world, getPosition(), speed, this);
            if (preyPos != null) {
                setPosition(preyPos);
                return;
            } else if (getFoodLevel() < 30) {
                Tile foodTile = WorldSearch.nearestFood(world, getPosition(), speed);
                if (foodTile != null) {
                    Coord foodPos = new Coord(foodTile.getX(), foodTile.getY());
                    setPosition(foodPos);
                    return;
                }
            }
        }

        randomMove(world, this);
    }


    //przyspieszenie gdy znajdzie jedzenie w odleglosci do 3 kratek
    private boolean isFoodNearby(World world) {
        int x = getPosition().x;
        int y = getPosition().y;
        int range = 3;

        for (int dx = -range; dx <= range; dx++) {
            for (int dy = -range; dy <= range; dy++) {
                int newX = x + dx;
                int newY = y + dy;

                if (newX >= 0 && newX < world.getWidth() && newY >= 0 && newY < world.getHeight()) {
                    Coord candidate = new Coord(newX, newY);
                    Tile tile = world.getTile(candidate);
                    if (tile != null && tile.hasFood()) return true;
                }
            }
        }
        return false;
    }

    //grafika

    // wiek kiedy orka sie robi dorosla
    private static final int AGE_OLD = 18;

    //laduje baby i adult orke
    private static Image BabyOrca;
    private static Image AdultOrca;

    private final ImageView imageView = new ImageView();
    public ImageView getImageView() {
        return imageView;
    }

    private static void loadImagesIfNeeded() {
        if (BabyOrca == null || AdultOrca == null) {
            if (!GraphicsEnvironment.isHeadless()) { //sprawdza czy jest srodowisko graficzne (testy go nie mają)
                BabyOrca = new Image(Objects.requireNonNull(Orca.class.getResource("/images/babyOrca.png")).toExternalForm());
                AdultOrca = new Image(Objects.requireNonNull(Orca.class.getResource("/images/adultOrca.png")).toExternalForm());
            }
        }
    }

    private void settings() {
        imageView.setPreserveRatio(true);
        updateOrcaGraphics();
    }

    private void updateOrcaGraphics() {
        loadImagesIfNeeded();

        boolean isYoung = getAge() < AGE_OLD;
        imageView.setImage(isYoung ? BabyOrca : AdultOrca);

        double scale = isYoung ? 0.7 : 1.0; //baby orka jeszcze mniejsza hahah
        imageView.setFitWidth(Main.getTileSize() * scale);
        imageView.setFitHeight(Main.getTileSize() * scale);
    }
}