package allAnimals;

import body.Animal;
import body.Genes;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import map.Coord;
import ocean.Main;
import ocean.SimulationStatsManager;
import ocean.World;

import java.awt.*;
import java.util.List;
import java.util.Objects;

public class TralaleroTralala extends Animal {

    public TralaleroTralala(Coord position) {
        super(position, generateGenes());
        setName("TralaleroTralala");
        settings();
    }

    private static Genes generateGenes() {
        Genes genes = new Genes();
        genes.setStrength(1000000);
        genes.setSpeed(4);
        genes.setMaxAge(10);
        genes.setMaxLoneliness(1000000);
        genes.setMaxEnergy(1000000);
        return genes;
    }


    public void update(World world) {
        this.setAge(this.getAge() + 1);

        if (this.getAge() >= this.getGenes().getMaxAge()) {
            this.die(world);
            SimulationStatsManager.writeToFile("TralaleroTralala," + this.getId() + ",disappeared\n");
            return;
        }

        int distance = this.getGenes().getSpeed();
        int dx = World.random.nextBoolean() ? distance : -distance; //losowanie +-speed
        int dy = World.random.nextBoolean() ? distance : -distance;
        int newX = Math.min(Math.max(0, this.getPosition().x + dx), world.getWidth() - 1); //granice
        int newY = Math.min(Math.max(0, this.getPosition().y + dy), world.getHeight() - 1);
        Coord newPos = new Coord(newX, newY);
        this.setPosition(newPos);

        //usuwa zwierzeta
        List<Animal> nearby = world.getNearbyAnimals(newPos, this.getGenes().getSpeed());
        for (Animal animal : nearby) {
            if (animal != this && animal.isAlive()) {
                animal.die(world);
                SimulationStatsManager.writeToFile("TralaleroTralala," + this.getId() + ",killed," + animal.getName() + "," + animal.getId() + "\n");
            }
        }
    }



    /* -------------------------------GRAPHICS------------------------------- */

    private static Image boss;

    private final ImageView imageView = new ImageView();
    public ImageView getImageView() { return imageView; } // getter


    /* -------------------------------GUI------------------------------- */

    private static void loadImagesIfNeeded() {
        if (boss == null) {
            if (!GraphicsEnvironment.isHeadless()) { //sprawdza czy jest srodowisko graficzne (testy go nie mają)
                boss = new Image(Objects.requireNonNull(Nemo.class.getResource("/images/Tralalero_Tralala.png")).toExternalForm());
            }
        }
    }

    private void settings() {
        imageView.setPreserveRatio(true);
        updateNemoGraphics();
    }

    private void updateNemoGraphics() {
        loadImagesIfNeeded();

        imageView.setImage(boss);

        imageView.setFitWidth(Main.getTileSize() * 3);
        imageView.setFitHeight(Main.getTileSize() * 3);
    }

    @Override
    public Animal giveBirth(Coord pos, Animal parent1, Animal parent2) { return null; }
}

