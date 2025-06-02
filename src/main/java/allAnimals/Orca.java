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
    private int boostTurns = 0;//przyspieszenie ile zostalo
    private int boostWaiting = 0; //odpoczynek po booscie


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
        Genes genes = new Genes();
        genes.setStrength(Genes.mutate(70));
        genes.setSpeed(5);
        genes.setMaxAge(Genes.mutate(90));
        genes.setMaxLoneliness(Genes.mutate(50));
        genes.setMaxEnergy(100);
        return genes;
    }


    @Override
    public void update(World world) {
        processLifeCycle(world);
        updateOrcaGraphics();
        if (!isAlive()) { return; }

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


    //mechanika ruchu
    @Override
    public void move(World world) {
        int speed = getGenes().getSpeed();

        boolean preyNearby = false;
        //jak odpoczela i nie ma przyspieszenia to szuka nowej ofiary
        if (boostTurns == 0 && boostWaiting == 0) {
            preyNearby = WorldSearch.nearestPrey(world, getPosition(), 3, this) != null;
        }

        //boost i odpoczynek
        if (boostTurns > 0) {
            boostTurns--;
            speed += 3;
            if (boostTurns == 0) {
                boostWaiting = 5;
            }
        } else if (boostWaiting > 0) {
            boostWaiting--;
        } else if (preyNearby) {
            boostTurns = 2;
            speed += 3;
        }

        if (getFoodLevel() < 70) {
            Coord preyPos = WorldSearch.nearestPrey(world, getPosition(), speed, this);
            if (preyPos != null) {
                setPosition(preyPos);
                return;
            }
        }

        randomMove(world, this);
    }



    /* -------------------------------GRAPHICS------------------------------- */

    private static final int AGE_OLD = 18;
    private static Image BabyOrca;
    private static Image AdultOrca;

    private final ImageView imageView = new ImageView();
    @Override
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

        double scale = isYoung ? 0.7 : 1.0;
        imageView.setFitWidth(Main.getTileSize() * scale);
        imageView.setFitHeight(Main.getTileSize() * scale);
    }
}