package body;

import allAnimals.Skeleton;
import ocean.World;

import java.util.List;

//obsługuje staty i cykl
public class AnimalLifeManager {
    static void lifeCycle(World world, Animal animal) {
        AnimalEffectsManager.applyPoisonEffect(animal);

        animal.setAge(animal.getAge() + 1);
        animal.setFoodLevel(animal.getFoodLevel() - 1);
        updateEnergy(animal);

        if (animal.getFoodLevel() <= 0 || animal.getEnergy() <= 0 || animal.getAge() > animal.getGenes().getMaxAge()) {
            SkeletonSettings(world, animal);
            return; //koniec
        }

        updateLoneliness(world, animal);
        updateHealth(animal);

        if (animal.getHealth() <= 0){
            SkeletonSettings(world, animal);
        }
    }


     //aktualizacja energii - zależna od jedzenia i zdrowia
     private static void updateEnergy(Animal animal) {
        int baseEnergy = (int)((animal.getFoodLevel() * 0.7) + (animal.getHealth() * 0.3));
        if (baseEnergy < 20) {
            baseEnergy = (int)(animal.getEnergy() * 0.6); //zmniejszenie energii przy krytycznym poziomie
        }
        animal.setEnergy(Math.max(0, baseEnergy)); //nie mniej niż 0
    }


    //sprawdzanie samotności - czy wokół są zwierzęta tego samego gatunku
    private static void updateLoneliness(World world, Animal animal) {
        List<Animal> nearby = world.getNearbyAnimals(animal.getPosition(), (int)(animal.getGenes().getSpeed() * 0.5)); //wartość przeszukiwania do zmiany
        boolean foundSameSpecies = false;
        for (Animal other : nearby) {
            if (other != animal && animal.getName().equals(other.getName())){
                foundSameSpecies = true;
                break;
            }
        }
        if (!foundSameSpecies) {animal.setLoneliness(animal.getLoneliness() + 5);} //nikogo nie ma :((
        else {animal.setLoneliness(0);} //reset samotności jeśli ktoś jest
    }


    //aktualizacja zdrowia - zależna od jedzenia i samotności
    private static void updateHealth(Animal animal) {
        if (animal.getFoodLevel() < 40 || animal.getLoneliness() >= animal.getGenes().getMaxLoneliness()){ //jeśli samotność osiągnęła max traci zdrowie co turę
            animal.setHealth(Math.max(animal.getHealth()-1, 0)); //zdrowie podupada (-1) z każdą turą
        } else if (animal.getLoneliness()>0 && animal.getLoneliness()%3==0){ //normalnie jest co 3 tury - do zmiany chyba bo idk czy matematycznie działa
            animal.setHealth(Math.max(animal.getHealth()-1, 0));
        }

        if (animal.getFoodLevel()>70 && animal.getEnergy()>20) { //zdrowie się odnawia (tak jakies 120% ale do zmiany)
            animal.setHealth((int) (Math.min(animal.getHealth()*1.2, 100)));
        }
    }

    static void SkeletonSettings(World world, Animal animal) {
        world.addAnimal(new Skeleton(animal.getPosition())); // dodajemy szkielet, dla konkretnego zwierzęcia, na konkretnej pozycji
        world.getAnimals().remove(animal); // usuwwamy ze świata
        animal.die(); // alive = false
    }
}
