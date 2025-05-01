package ocean;

import static ocean.Animal.rand;

//narzedzia do mechaniki walki
public class AnimalCombatUtils {

    protected static double getEffectiveStrength(Animal animal) {
        return animal.getGenes().getStrength() * (animal.getEnergy()/100.0);
    }

    protected static double getEffectiveSpeed(Animal animal) {
        return animal.getGenes().getSpeed() * (animal.getEnergy()/100.0);
    }

    protected static double getCombatPower(Animal animal) {
        return getEffectiveStrength(animal) * 0.7 + getEffectiveSpeed(animal) * 0.3;
    }


    protected static void takeDamage(double amount, Animal animal) {
        int newHealth = (int)(animal.getHealth() - amount);
        animal.setHealth(Math.max(newHealth, 0));

        if (animal.getHealth() <= 0) {
            animal.die(); //nie żyje
        }
    }


    //ucieczka z walki - nie podoba mi się że jest argument world ale idk jak to zrobić
    protected static void escape(World world, Animal animal) {
        Coord pos = animal.getPosition();
        int distance = (int) (1.2 * animal.getGenes().getSpeed()); //ma większą prędkość w walce minimalnie (adrenalina XD) - do zmiany możliwej

        Coord coralPos = world.nearestCoral(pos, distance);
        if (coralPos!=null) { //gdy istnieje rafa w zasięgu
            animal.setPosition(coralPos); //skok na rafę
        } else { //gdy nie ma rafy to losowy kierunek ucieczki na pełną odległość dlatego nie randomMove
            int dx = rand.nextBoolean() ? distance : -distance;
            int dy = rand.nextBoolean() ? distance : -distance;
            Coord escapePos = new Coord(pos.x + dx, pos.y + dy);
            animal.setPosition(world.inBounds(escapePos.x, escapePos.y) ? escapePos //robi set i w tym set jest warunek czy poza granice
                    : pos.randomAdjacent(world.getWidth(), world.getHeight(), animal.getGenes().getSpeed()));
        }
    }
}
