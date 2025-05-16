package extendedMechanics;

import body.Animal;
import body.Gender;
import body.Genes;
import map.Coord;
import ocean.World;
import allAnimals.Egg;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class Reproduction {

    /* -------------------------------CONSTANTS------------------------------- */

    public static final int MINIMAL_AGE_TO_GET_PREGNANT = 18;
    public static final double ENERGY_NEEDED = 0.8; // 80% * MaxEnergy
    public static final int SAFETY_DISTANCE = 5; // kratki góra, dół, boki, przekątne
    public static final int PREGNANCY_DURATION = 9; // 9 MONTHS - 9 TURNS

    /* -------------------------------CONDITIONS------------------------------- */

    public static boolean IsReady(Animal a) {
        return
                a.isAlive()
                && a.getAge() >= MINIMAL_AGE_TO_GET_PREGNANT
                && !a.isPregnant()
                && a.getEnergy() >= a.getMaxEnergy() * ENERGY_NEEDED;
    }

    /* -------------------------------DISTANCE NEEDED------------------------------- */

    // MUSZĄ BYĆ MAKSYMALNIE KRATKĘ OD SIEBIE, BY KOBITKA MOGŁA ZAJŚĆ W CIĄŻĘ
    public static boolean isDistance(Animal animal1, Animal animal2) {
        Coord pos1 = animal1.getPosition();
        Coord pos2 = animal2.getPosition();
        int x = Math.abs(pos1.getX() - pos2.getX());
        int y = Math.abs(pos1.getY() - pos2.getY());
        return x + y <= 1;
    }

    /* -------------------------------MECHANICS------------------------------- */

    public static void ReproductionProcess(World world, Animal animal1, Animal animal2) {
        // PŁEĆ I GATUNEK
        if (animal2 == null) return; // brak partnera/partnerki
        if (!animal1.getClass().equals(animal2.getClass())) return; // ten sam gatunek
        if (animal1.getGender() == animal2.getGender()) return; // ta sama płeć

        if (animal1.getId() == animal2.getId()) {
            return;
        }

        Animal female; // kobietka
        if (animal1.getGender() == Gender.FEMALE) {
            female = animal1;
        } else {
            female = animal2;
        }

        Animal male; // mężczyzna
        if (female == animal1) {
            male = animal2;
        } else {
            male = animal1;
        }

        // BEZPIECZNY DYSTANS
        if(!isDistance(animal1, animal2)) return;

        // CZY SPEŁNIAJĄ WARUNKI
        if (!IsReady(female) || !IsReady(male)) return; //odpowiedni wiek, poziom energii, status życia i status ciąży (TAK LUB NIE)

        // SPADEK ENERGII W ZWIĄZKU Z ZAPŁODNIENIEM
        int energy_loss_male = 5;
        int energy_loss_female = 10;

        male.setEnergy(Math.max(0, male.getEnergy() - energy_loss_male));
        female.setEnergy(Math.max(0, female.getEnergy() - energy_loss_female));

        // ZMIANA STATUSU - zaczęcie się ciąży
        female.setPregnant(true);
        female.setFatherDuringPregnancy(male);
        female.setPregnancyCounter(PREGNANCY_DURATION);

        System.out.println("CIĄŻA!");

    }

    /* -------------------------------PREGNANCY------------------------------- */
    public static void pregnancyTick(World world, Animal a) {
        if (a.getGender() != Gender.FEMALE || !a.isPregnant()) return; // czy kobietka i czy w ciąży obecnie
        a.setPregnancyCounter(a.getPregnancyCounter() - 1); // odejmowanie 'miesiąca' co turę

        if (a.getPregnancyCounter() == 0) { // czas rozwiązania
            spawnBaby(world, a, a.getFatherDuringPregnancy());
            a.setPregnant(false);
        }
    }

    /* -------------------------------BIRTH------------------------------- */

    public static void spawnBaby(World world, Animal mother, Animal father) {
        Coord spawn = findFreeTile(world, mother.getPosition());
        if (spawn == null) return; // idk, nie zrespi się

        Genes genes = Genes.inherit(mother.getGenes(), father.getGenes());
        Egg egg = new Egg(spawn, genes, 5); // 5 tur
        egg.setMother(mother);
        world.addEgg(egg);
        System.out.println("POTOMSTWO!");
    }

    /* -------------------------------SAFE SPACE------------------------------- */
    // TODO: trzeba dorobić agresorów
    public static boolean IsAreaSafe(World world, Animal self) {
        return true;
    }

    public static Coord findFreeTile(World world, Coord pos) {
        int[][] directions = {{1,0}, {-1,0}, {0,1}, {0,-1}}; // kierunki: góra, dół, prawo, lewo
        Random random = new Random();

        List<Coord> possibleTiles = new ArrayList<>();
        for (int[] d: directions) {
            Coord c = new Coord(pos.getX() + d[0], pos.getY() + d[1]);
            if (world.inBounds(c.x, c.y) && !world.isOccupied(c)) {
                possibleTiles.add(c);
            }
        }

        if (possibleTiles.isEmpty()) return null;

        return possibleTiles.get(random.nextInt(possibleTiles.size()));
    }
}
