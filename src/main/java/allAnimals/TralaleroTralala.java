package allAnimals;

import body.Animal;
import body.Genes;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import map.Coord;
import map.MapType;
import map.Tile;
import ocean.Main;
import ocean.SimulationStatsManager;
import ocean.World;
import soundEffects.SoundPlayer;

import java.awt.*;
import java.util.List;
import java.util.Objects;

//inheritance from Animal
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


    public void update(World world) {  //polymorphism
        this.setAge(this.getAge() + 1);

        if (this.getAge() >= this.getGenes().getMaxAge()) {
            SoundPlayer.playSound("sounds/oof.wav");
            this.die(world);
            SimulationStatsManager.writeToFile("TralaleroTralala," + this.getId() + ",disappeared\n");
            return;
        }

        int distance = this.getGenes().getSpeed();
        int dx = World.random.nextBoolean() ? distance : -distance; //losowanie +-speed
        int dy = World.random.nextBoolean() ? distance : -distance;
        int newX = Math.min(Math.max(0, this.getPosition().getX() + dx), world.getWidth() - 1); //granice
        int newY = Math.min(Math.max(0, this.getPosition().getY() + dy), world.getHeight() - 1);
        Coord newPos = new Coord(newX, newY);
        this.setPosition(newPos); //przesuwa się

        //usuwa zwierzeta
        List<Animal> nearby = world.getNearbyAnimals(newPos, this.getGenes().getSpeed()); //pobiera listę zwierzat do okoła
        for (Animal animal : nearby) {
            if (animal != this && animal.isAlive()) { //jeśli zwierze to nie on i żyje
                Tile tile = world.getTile(animal.getPosition());
                if (tile != null && tile.getMapType() != MapType.CORAL) { //jeżeli nie jest to CORAL
                    SoundPlayer.playSound("sounds/oof.wav");
                    animal.die(world); //zabija
                    SimulationStatsManager.writeToFile("TralaleroTralala," + this.getId() + ",killed," + animal.getName() + "," + animal.getId() + "\n");
                }
            }
        }
    }

    @Override
    public Animal giveBirth(Coord pos, Animal parent1, Animal parent2) { return null; }  //polymorphism



    /* -------------------------------GRAPHICS------------------------------- */

    private static Image boss;

    private final ImageView imageView = new ImageView();
    @Override
    public ImageView getImageView() { return imageView; } // getter


    private static void loadImagesIfNeeded() {
        if (boss == null) {
            if (!GraphicsEnvironment.isHeadless()) { //sprawdza czy jest srodowisko graficzne (testy go nie mają)
                boss = new Image(Objects.requireNonNull(Nemo.class.getResource("/images/Tralalero_Tralala.png")).toExternalForm());
            }
        }
    }

    private void settings() {
        imageView.setPreserveRatio(true);
        updateBossGraphics();
    }

    private void updateBossGraphics() {
        loadImagesIfNeeded();

        imageView.setImage(boss);

        imageView.setFitWidth(Main.getTileSize() * 3);
        imageView.setFitHeight(Main.getTileSize() * 3);
    }
}

