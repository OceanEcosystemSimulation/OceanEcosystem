package movement;

import body.Animal;
import ocean.World;

import java.util.List;

public interface IFight {
    boolean canAttack(Animal other);
    boolean attack(Animal target, World world);
    int calculateGain(Animal animal) ;

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

