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
/**
 * Represents the powerful and legendary TralaleroTralala, a special entity in the simulation.
 * Has exceptionally high stats and eliminates nearby animals while moving.
 * Inherits from Animal.
 */

public class TralaleroTralala extends Animal {
    public TralaleroTralala(Coord position) {
        super(position, generateGenes());
        setName("TralaleroTralala");
        settings();
    }

    /**
     * Generates default genetic attributes.
     * @return The genes object containing attributes.
     */
    private static Genes generateGenes() {
        Genes genes = new Genes();
        genes.setStrength(1000000);
        genes.setSpeed(3);
        genes.setMaxAge(10);
        genes.setMaxLoneliness(1000000);
        genes.setMaxEnergy(1000000);
        return genes;
    }


    /**
     * Updates TralaleroTralala's lifecycle in the simulation.
     * Moves randomly and eliminates nearby animals except those on coral tiles.
     * Plays a sound effect when an animal is removed.
     * @param world The world in which the animal exists.
     */

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


    /**
     * Loads animal images if not already loaded or if there is graphic environment.
     */
    private static void loadImagesIfNeeded() {
        if (boss == null) {
            if (!GraphicsEnvironment.isHeadless()) { //sprawdza czy jest srodowisko graficzne (testy go nie mają)
                boss = new Image(Objects.requireNonNull(Nemo.class.getResource("/images/Tralalero_Tralala.png")).toExternalForm());
            }
        }
    }


    /**
     * Initializes graphical settings.
     */
    private void settings() {
        imageView.setPreserveRatio(true);
        updateBossGraphics();
    }

    /**
     * Updates the graphical representation of TralaleroTralala.
     */
    private void updateBossGraphics() {
        loadImagesIfNeeded();

        imageView.setImage(boss);

        imageView.setFitWidth(Main.getTileSize() * 3);
        imageView.setFitHeight(Main.getTileSize() * 3);
    }
}

