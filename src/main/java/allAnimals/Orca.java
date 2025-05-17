package allAnimals;

import body.*;
import map.*;
import ocean.Main;
import ocean.World;
import extendedMechanics.Reproduction;
import ocean.WorldSearch;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.List;
import java.util.Objects;

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
        g.setSpeed(70 + rand.nextInt(11)); //mozna zmienic
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

        handleFeeding(world); //je jesli moze
        handleMating(world);
        handleMovement(world);
    }

    private void handleFeeding(World world) {
        tryToEat(world);
    }

    private void handleMating(World world) {
        int radius = getGenes().getSpeed();
        Animal mate = WorldSearch.nearestMate(world, getPosition(), radius, this);
        Reproduction.ReproductionProcess(this, mate);
    }

    private void handleMovement(World world) {
        move(world);
    }

    @Override
    public Animal giveBirth(Coord position, Animal parent1, Animal parent2) {
        return new Orca(position, parent1, parent2);
    }

    //atak
    private void tryToAttack(World world) {
        List<Animal> nearbyAnimals = world.getNearbyAnimals(getPosition(), 0); // pobiera zwierzęta na aktualnym polu
        for (Animal animal : nearbyAnimals) {
            if (animal != this && canAttack(animal)) {
                System.out.println("Orca id: " + getId() + " attacks " + animal.getName() + " id: " + animal.getId());
                if (attack(animal, world)) {
                    int gain = calculateGain(animal);
                    setFoodLevel(getFoodLevel() + gain);
                    return;
                }
            }
        }
    }

    //taka sama zasada jak w rekinie haha
    private static final List<String> preyList = List.of("Fish", "Shark");

    @Override
    public boolean canAttack(Animal other) {
        return other != null && preyList.contains(other.getName());
    }

    private int calculateGain(Animal animal) {
        return switch (animal.getName()) {
            case "Fish" -> 30; //przykladowo
            case "Shark" -> 40;
            default -> 0;
        };
    }

    @Override
    public void eat(Tile tile) { //wiem że to nic nie daje ale bez tego pokazywało że "public class orca ..." jest błędnie
        //po prostu nic nie je z mapy
    }


    //przyspieszenie gdy znajdzie jedzenie w odleglosci do 3 kratek
    public int getSpeedForFood(World world) {
        if (world != null) {
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

                        if (tile != null && tile.hasFood()) {
                            return getGenes().getSpeed() + 10; //boost
                        }
                    }
                }
            }
        }
        return getGenes().getSpeed(); // brak boosta
    }

    //grafika

    // wiek kiedy orka sie robi dorosla
    private static final int AGE_OLD = 18;

    //laduje baby i adult orke
    private static final Image BabyOrca = new Image(Objects.requireNonNull(Orca.class.getResource("/images/babyOrca.png")).toExternalForm());
    private static final Image AdultOrca = new Image(Objects.requireNonNull(Orca.class.getResource("/images/adultOrca.png")).toExternalForm());

    private final ImageView imageView = new ImageView();
    public ImageView getImageView() {
        return imageView;
    }

    private void settings() {
        imageView.setPreserveRatio(true);
        updateOrcaGraphics();
    }

    private void updateOrcaGraphics() {
        boolean isYoung = getAge() < AGE_OLD;
        imageView.setImage(isYoung ? BabyOrca : AdultOrca);

        double scale = isYoung ? 0.7 : 1.0; //baby orka jeszcze mniejsza hahah
        imageView.setFitWidth(Main.getTileSize() * scale);
        imageView.setFitHeight(Main.getTileSize() * scale);
    }

}
