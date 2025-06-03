package movement;

import body.Animal;
import ocean.World;

import java.util.List;

// fight - interface
/**
 * Interface for combat mechanics.
 */
public interface IFight {
    /**
     * Determines if the animal can attack the specified target.
     * Only animals listed in `preyList` are valid targets.
     * @param other The potential target animal.
     * @return True if the target is in the prey list, otherwise false.
     */
    boolean canAttack(Animal other);

    /**
     * Attempts to attack targeted animal.
     * @param target The animal being attacked.
     * @param world The simulation world in which it happens.
     * @return True if the target is killed, otherwise false.
     */
    boolean attack(Animal target, World world);

    /**
     * Calculates gain value from eaten animal based on its name.
     * @param animal The eaten animal from which gain is being calculated.
     * @return The gain value based on the animal's name.
     */
    int calculateGain(Animal animal);

    /**
     * Default method for attempt to attack a nearby animal.
     * If a valid target is found and successfully attacked, food level increases.
     * @param world The simulation world in which it happens.
     * @param self The attacking animal.
     */
    default void tryToAttack(World world, Animal self) {
        List<Animal> nearbyAnimals = world.getNearbyAnimals(self.getPosition(), 0); //pobiera zwierzęta na aktualnym polu
        for (Animal animal : nearbyAnimals) {
            if (animal!=this && canAttack(animal)) { //nie zjada sam siebie
                if (attack(animal, world)) { //udany atak
                    int gain = calculateGain(animal); //obliczanie gain z ataku na zwierzę
                    self.setFoodLevel(self.getFoodLevel() + gain); //aktualizacja poziomu jedzenia
                    return; //koniec akcji
                }
            }
        }
    }
}

