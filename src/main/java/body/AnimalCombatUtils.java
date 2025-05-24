package body;

import map.*;
import ocean.World;
import ocean.WorldSearch;

import static body.Animal.rand;

//narzedzia do mechaniki walki
public class AnimalCombatUtils {

    static double getEffectiveStrength(Animal animal) {
        return animal.getGenes().getStrength() * (animal.getEnergy()/100.0);
    }

    static double getEffectiveSpeed(Animal animal) {
        return animal.getGenes().getSpeed() * (animal.getEnergy()/100.0);
    }

    static double getCombatPower(Animal animal) {
        return getEffectiveStrength(animal) * 0.7 + getEffectiveSpeed(animal) * 0.3;
    }


    static void takeDamage(World world, Animal animal, double amount) {
        int newHealth = (int)(animal.getHealth() - amount);
        animal.setHealth(Math.max(newHealth, 0));

        if (animal.getHealth() <= 0) {
            AnimalLifeManager.SkeletonSettings(world, animal);
        }
    }


    //ucieczka z walki - nie podoba mi się że jest argument world ale idk jak to zrobić
    static void escape(World world, Animal animal) {
        Coord pos = animal.getPosition();
        int distance = animal.getGenes().getSpeed();

        Coord coralPos = WorldSearch.nearestCoral(world, pos, distance);
        if (coralPos!=null) { //gdy istnieje rafa w zasięgu
            animal.setPosition(coralPos); //skok na rafę
        } else { //gdy nie ma rafy to losowy kierunek ucieczki na pełną odległość dlatego nie randomMove
            int dx = rand.nextBoolean() ? distance : -distance;
            int dy = rand.nextBoolean() ? distance : -distance;
            Coord escapePos = new Coord(pos.x + dx, pos.y + dy);

            //jesli w granicach i nikogo tam nie ma to skacze na pełną odl w innym wypadku losowo
            if (world.inBounds(escapePos.x, escapePos.y) && !world.isOccupied(escapePos)) {
                animal.setPosition(escapePos);
            } else {
                Coord randomEscapePos = pos.randomAdjacent(world.getWidth(), world.getHeight(), animal.getGenes().getSpeed(), world);
                if (randomEscapePos == null) { //jeśli zwróciło null czyli nie ma już miejcsc
                    world.endSimulation(); //kończy symulację
                    return;
                }
                animal.setPosition(randomEscapePos);
            }
        }
    }

    //losowy ruch w zasięgu speed
    public static void randomMove(World world, Animal animal) {
        Coord newPos = animal.getPosition().randomAdjacent(world.getWidth(), world.getHeight(), animal.getGenes().getSpeed(), world); //generuje nową losową pozycję sąsiednią
        if (newPos == null) { //jeśli zwróciło null czyli nie ma już miejcsc
            world.endSimulation(); //kończy symulację
            return;
        }
        animal.setPosition(newPos); //ustawia pozycję
    }
}
