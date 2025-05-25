package allAnimals;

import body.*;
import extendedMechanics.Reproduction;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import map.*;
import movement.IFight;
import movement.IEat;
import ocean.Main;
import ocean.World;

import java.awt.*;
import java.util.List;
import java.util.Objects;

public class OceanicPuffer extends Omnivorous implements IEat, IFight {
    public OceanicPuffer(Coord position) {
        super(position, generateGenes());
        setName("OceanicPuffer");
        settings();
    }

    public OceanicPuffer(Coord position, Animal parent1, Animal parent2) {
        super(position, parent1, parent2);
        setName("OceanicPuffer");
        settings();
    }

    private static Genes generateGenes() {
        Genes genes = new Genes();
        genes.setStrength(20);//mozna zmienic
        genes.setSpeed(70);
        genes.setMaxAge(30);
        genes.setMaxLoneliness(50);
        genes.setMaxEnergy(100);
        return genes;
    }


    @Override
    public Animal giveBirth(Coord position, Animal parent1, Animal parent2) {
        return new OceanicPuffer(position, parent1, parent2);
    }

    @Override
    public void update(World world) {
        processLifeCycle(world);
        updateGraphics();
        if (!isAlive()) return;

        Reproduction.pregnancyTick(world, this);

        tryToEat(world, this);
        tryToMate(world);
        move(world);
        tryToAttack(world, this);
    }


    private static final List<String> preyList = List.of("Crab");


    //walka
    @Override
    public boolean canAttack(Animal other) {
        return other != null && other.getName() != null && preyList.contains(other.getName());
    }


    @Override
    public boolean attack(Animal target, World world) {
        double attackerPower = this.getGenes().getStrength() * (this.getEnergy() / 100.0); //sila oceanicpuffer
        double targetPower = target.getGenes().getStrength() * (target.getEnergy() / 100.0); //sila przeciwnika

        if (attackerPower > targetPower) {
            target.die();
            System.out.println(getName() + " id: " + getId() + " killed " + target.getName() + " id: " + target.getId());
            return true;
        } else {
            this.die();
            System.out.println(target.getName() + " id: " + target.getId() + " killed " + getName() + " id: " + getId());

            //jesli przegra to zatrywa przeciwnika
            target.setPoisoned(true);
            System.out.println(target.getName() + " id: " + target.getId() + " got poisoned by dying Puffer!");

            return false;
        }
    }


    @Override
    public int calculateGain(Animal animal) {
        return switch (animal.getName()) {
            case "Crab" -> 25;
            default -> 5;
        };
    }


    //jedzenie
    @Override
    public boolean canEat(Tile tile) {
        return getFoodLevel() <= 70 && ( tile.getFoodType() == FoodType.PLANKTON || tile.getFoodType() == FoodType.ALGAE);
    }


    @Override
    public void eat(Tile tile, World world) {
        int gain = switch (tile.getFoodType()) {
            case PLANKTON -> 10;
            case ALGAE -> 15;
            default -> 0;
        };

        if (getFoodLevel() + gain <= 100) {
            setFoodLevel(getFoodLevel() + gain);
            System.out.println(this.getName() + " id: " + this.getId() + " eats " + tile.getFoodType());
            tile.clearFood(world);
        }
    }


    //grafika
    private static Image babyImage;
    private static Image adultImage;
    private static final int AGE_OLD = 18;

    private final ImageView imageView = new ImageView();
    public ImageView getImageView() {
        return imageView;
    }


    private static void loadImagesIfNeeded() {
        if (babyImage == null || adultImage == null) {
            if (!GraphicsEnvironment.isHeadless()) {
                babyImage = new Image(Objects.requireNonNull(OceanicPuffer.class.getResource("/images/BabyOceanicPuffer1.png")).toExternalForm());
                adultImage = new Image(Objects.requireNonNull(OceanicPuffer.class.getResource("/images/AdultOceanicPuffer.png")).toExternalForm());
            }
        }
    }

    private void settings() {
        imageView.setPreserveRatio(true);
        updateGraphics();
    }

    private void updateGraphics() {
        loadImagesIfNeeded();
        boolean isYoung = getAge() < AGE_OLD;
        if (isYoung) {
            imageView.setImage(babyImage);
        } else {
            imageView.setImage(adultImage);
        }
        double scale;
        if (isYoung) {
            scale = 0.8;
        } else {
            scale = 1.0;
        }
        imageView.setFitWidth(Main.getTileSize() * scale);
        imageView.setFitHeight(Main.getTileSize() * scale);
    }

}
