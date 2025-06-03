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

//inheritance from Carnivorous
/**
 * Represents an Octopus, inheriting from Carnivorous.
 */
public class Octopus extends Carnivorous {
    //encapsulation
    private int inkCooldown = 0; //ile tur zostało do odnowienia chmurki

    public Octopus(Coord position) {
        super(position, generateGenes());
        setName("Octopus");
        settings();

    }

    //konstruktor dziecka
    public Octopus(Coord position, Animal parent1, Animal parent2) {
        super(position, parent1, parent2);
        setName("Octopus");
        settings();
    }


    /**
     * Generates default genetic attributes.
     * @return The genes object containing attributes.
     */
    private static Genes generateGenes() {
        Genes genes = new Genes();
        genes.setStrength(40);
        genes.setSpeed(3);
        genes.setMaxAge(40);
        genes.setMaxLoneliness(50);
        genes.setMaxEnergy(90);
        return genes;
    }



    /* -------------------------------INK------------------------------- */


    /**
     * Releases an ink cloud if the cooldown has expired.
     * The ink affects nearby Dolphins within a radius of 2 tiles.
     * Ensures that no duplicate ink clouds are created for the same target.
     * @param world The simulation world in which it happens.
     */
    private void releaseInk(World world) {
        if (inkCooldown > 0) return; //nie może jeszcze wypuscic chmurki

        //sprwdza zwierzeta w promieniu dwoch kratek
        for (Animal a : world.getNearbyAnimals(getPosition(), 2)) {
            //sprawdza czy tam jest delfin i sprawdza czy to jest klasa delfina a nie np. jajka z delfinem
            if (a.isAlive() && !a.equals(this) && a.getClass() == Dolphin.class) {
                boolean alreadyInked = false; //zeby nie zatrzymywalo delfina jak juz jest zatrzymany
                for (WorldObject object : world.getObjects()) { //przechodzi przez swiat
                    if (object instanceof InkCloud) { //sprawdza czy dany obiekt ma chmurke
                        InkCloud ink = (InkCloud) object;
                        if (ink.getTarget() == a) {
                            alreadyInked = true;
                            break;
                        }
                    }
                }//powinno zapobiegac tworzeniu sie chmurek na sobie

                if (!alreadyInked) {
                    world.addObject(new InkCloud(a.getPosition(), a));
                    inkCooldown = 5; //musi odczekac 5 rund bo zostala wypuszczona chmurka
                }
                break;
            }
        }
    }


    /* -------------------------------LIFE------------------------------- */

    @Override
    public void update(World world) {  //polymorphism
        if(inkCooldown>0) {
            inkCooldown--;
        }
        processLifeCycle(world);
        updateOctopusGraphics();
        if (!isAlive()) { return; }

        Reproduction.pregnancyTick(world, this);

        tryToMate(world); //wywołanie mechaniki rozmnażania
        move(world); //wywołanie mechaniki ruchu
        tryToAttack(world, this); //wywołanie mechaniki ataku
        releaseInk(world);
    }


    @Override
    public Animal giveBirth(Coord position, Animal parent1, Animal parent2) {  //polymorphism
        return new Octopus(position, parent1, parent2);
    }




    private static final List<String> preyList = List.of("Nemo", "Shark"); //lista kogo atakuje


    @Override
    public boolean canAttack(Animal other) {
        return other != null && other.getName() != null && preyList.contains(other.getName());
    }


    @Override
    public int calculateGain(Animal animal) {  //polymorphism
        return switch (animal.getName()) {
            case "Nemo" -> 30;
            case "Shark" -> 60;
            default -> 0;
        };
    }


    /* -------------------------------GRAPHICS------------------------------- */

    private static Image babyOctopus;
    private static Image Octopus;

    private final ImageView imageView = new ImageView();
    @Override
    public ImageView getImageView() { return imageView; }

    /**
     * Loads animal images if not already loaded or if there is graphic environment.
     */
    private static void loadImagesIfNeeded() {
        if (babyOctopus == null || Octopus == null) {
            if (!GraphicsEnvironment.isHeadless()) { //sprawdza czy jest srodowisko graficzne (testy go nie mają)
                babyOctopus = new Image(Objects.requireNonNull(Octopus.class.getResource("/images/babyOctopus.png")).toExternalForm());
                Octopus = new Image(Objects.requireNonNull(Octopus.class.getResource("/images/Octopus.png")).toExternalForm());
            }
        }
    }

    /**
     * Initializes graphical settings.
     */
    private void settings() {
        imageView.setPreserveRatio(true);
        updateOctopusGraphics();
    }

    /**
     * Updates the graphical representation of the animal based on its age.
     */
    private void updateOctopusGraphics() {
        loadImagesIfNeeded();

        boolean isYoung = getAge() < getMajority();
        imageView.setImage(isYoung ? babyOctopus : Octopus);

        double scale = isYoung ? 0.8 : 1.0;
        imageView.setFitWidth(Main.getTileSize() * scale);
        imageView.setFitHeight(Main.getTileSize() * scale);
    }
}

