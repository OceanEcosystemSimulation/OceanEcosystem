package allAnimals;

import body.*;
import extendedMechanics.Reproduction;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import map.*;
import ocean.Main;
import ocean.World;

import java.awt.*;
import java.util.List;
import java.util.Objects;


public class Seal extends Carnivorous {

    public Seal(Coord position) {
        super(position, generateGenes());
        setName("Seal");
        settings();
    }

    public Seal(Coord position, Animal parent1, Animal parent2) {
        super(position, parent1, parent2);
        setName("Seal");
        settings();
    }

    private static Genes generateGenes() {
        Genes g = new Genes();
        g.setStrength(40);
        g.setSpeed(70);
        g.setMaxAge(30);
        g.setMaxLoneliness(45);
        g.setMaxEnergy(100);
        return g;
    }

    @Override
    public Animal giveBirth(Coord position, Animal parent1, Animal parent2) {
        return new Seal(position, parent1, parent2);
    }

    private static final List<String> preyList = List.of("Nemo", "Crab");

    @Override
    public boolean canAttack(Animal other) {
        return other != null && preyList.contains(other.getName());
    }

    @Override
    public int calculateGain(Animal animal) {
        return switch (animal.getName()) {
            case "Nemo" -> 15;
            case "Crab" -> 20;
            default -> 0;
        };
    }


    private void scareNearbySharks(World world) {
        int radius = 3;
        int sealX = getPosition().x;
        int sealY = getPosition().y;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                int x = sealX + dx;
                int y = sealY + dy;

                //sprawdza czy kratka nie wychodzi poza mape
                if (x >= 0 && x < world.getWidth() && y >= 0 && y < world.getHeight()) {
                    //szuka czy jest rekin na tej pozycji
                    for (Animal animal : world.getAnimals()) {
                        if (animal.isAlive() &&
                                animal.getPosition().x == x &&
                                animal.getPosition().y == y &&
                                animal.getName().equals("Shark")) {

                            System.out.println("Seal id: " + getId() + " scared away Shark id: " + animal.getId());

                            //przesuwa rekina na wylosowana kratke o wspolrzednych  rx i ry
                            int rx = (int)(Math.random() * world.getWidth());
                            int ry = (int)(Math.random() * world.getHeight());
                            animal.setPosition(new Coord(rx, ry));

                            break; //jak znajdzie rekina to konczy
                        }
                    }
                }
            }
        }
    }



    @Override
    public void update(World world) {
        processLifeCycle(world);
        scareNearbySharks(world);
        updateSealGraphics();
        if (!isAlive()) return;

        Reproduction.pregnancyTick(world, this);

        tryToMate(world);
        tryToAttack(world, this);
        move(world);
    }

    /* ---------- GRAFIKA ---------- */

    private static final int AGE_OLD = 18;
    private static Image babySeal;
    private static Image adultSeal;

    private final ImageView imageView = new ImageView();
    public ImageView getImageView() {
        return imageView;
    }

    private static void loadImagesIfNeeded() {
        if (babySeal == null || adultSeal == null) {
            if (!GraphicsEnvironment.isHeadless()) {
                babySeal = new Image(Objects.requireNonNull(Seal.class.getResource("/images/babySeal.png")).toExternalForm());
                adultSeal = new Image(Objects.requireNonNull(Seal.class.getResource("/images/adultSeal.png")).toExternalForm());
            }
        }
    }

    private void settings() {
        imageView.setPreserveRatio(true);
        updateSealGraphics();
    }

    private void updateSealGraphics() {
        loadImagesIfNeeded();

        boolean isYoung = getAge() < AGE_OLD;
        if (isYoung) {
            imageView.setImage(babySeal);
        } else {
            imageView.setImage(adultSeal);
        }

        double scale;
        if (isYoung) {
            scale = 0.75;
        } else {
            scale = 1.0;
        }
        imageView.setFitWidth(Main.getTileSize() * scale);
        imageView.setFitHeight(Main.getTileSize() * scale);
    }
}
