package ocean;

import body.*;
import map.Coord;
import map.MapType;
import map.Tile;

import java.util.ArrayList;
import java.util.List;

//wszystkie przeszukiwania mapy
public class WorldSearch {

    //znajdywanie alg lub planktonu w okreslonym promieniu
    public static Tile nearestFood(World world, Coord position, int radius) {
        Tile nearestFood = null; //zmenna z najblizszym jedzeniem
        int minDist = Integer.MAX_VALUE; //najmniejsza odległość od pozycji tego co je

        for (int dx=-radius; dx<=radius; dx++) { //iteracja po promieniu
            for (int dy=-radius; dy<=radius; dy++) {
                Coord candidate = new Coord(position.x + dx, position.y + dy); //wspołrzedne kandydata na ofiarę
                if (world.inBounds(candidate.x, candidate.y)) { //czy współrzędne w granicach mapy
                    Tile tile = world.getTile(candidate); //pobranie pola
                    if (tile != null && tile.hasFood() && !world.isOccupied(candidate)) { //sprawdza czy jest na nim jedzenie i czy ktos tam jest
                        int dist = position.distance(candidate); //odległość od candidate
                        if (dist < minDist) { //jeżeli ma mniejszy dystans niż wcześniejsze to bierze to
                            minDist = dist;
                            nearestFood = tile;
                        }
                    }
                }
            }
        }
        return nearestFood; //zwaca najbliższe pole z jedzeniem
    }



    //szuka najbliższego organizmu do zjedzenia (ofiarę)
    public static Coord nearestPrey(World world, Coord position, int radius, Carnivorous predator) {
        Animal nearestPrey = null; //zmenna z najblizszą ofiarą
        int minDist = Integer.MAX_VALUE; //najmniejsza odległość od pozycji

        for (Animal animal : world.getNearbyAnimals(position, radius)) { //iteracja po liście zwierząt w promieniu
            Tile preyTile = world.getTile(animal.getPosition()); //pole na którym jest ofiara
            if (preyTile!=null && preyTile.getMapType()!= MapType.CORAL && predator.canAttack(animal)) { //sprawdzenie czy może zaatakować i czy to nie rafa
                int dist = position.distance(animal.getPosition()); //odległość od prey
                if (dist < minDist) { //jeśli bliżej to bierze
                    minDist = dist;
                    nearestPrey = animal;
                }
            }
        }
        return nearestPrey!=null ? nearestPrey.getPosition() : null; //zwraca współrzędne ofiary lub null jak jej nie ma
    }


    //znajduje wszystkich mate w promieniu
    public static List<Animal> allMatesInRange(World world, Animal self, Coord position, int radius) {
        List<Animal> mates = new ArrayList<>(); //lista na mates
        for (Animal animal : world.getNearbyAnimals(position, radius)) { //iteracja po zwierzetach w promieniu
            if (animal!=null && animal!=self && animal.getName() != null && self.getName() != null && animal.getName().equals(self.getName())) { //porównywanie gatunków
                mates.add(animal);
            }
        }
        return mates; //zwraca tabelę z mates
    }


    //DO USUNIECIA PRAWDOPODOBNIE ALE NA RAZIE NIECH BEDZIE
    //znajduje zwierze w pobliżu - prawdopodobnie mozna to połączyć z nearestPrey <-- do zmiany póżniej moze
    public static Animal nearestMate(World world, Coord position, int radius, Animal self) {
        Animal nearestMate = null; //zmenna z najblizszym mate
        int minDist = Integer.MAX_VALUE; //najmniejsza odległość od pozycji

        for (Animal animal : world.getNearbyAnimals(position, radius)) { //iteracja po liście zwierząt w promieniu
            if (animal!=null && animal!=self && animal.getName() != null && self.getName() != null && animal.getName().equals(self.getName())) { //porównywanie gatunków
                int dist = position.distance(animal.getPosition()); //odległość od mate
                if (dist < minDist) {
                    minDist = dist;
                    nearestMate = animal;
                }
            }
        }
        return nearestMate; //zwraca mate
    }


    //szuka najblizszego pola CORAL w promieniu
    public static Coord nearestCoral(World world, Coord position, int radius) {
        Coord nearestCoral = null; //zmienna do przechowywania coords tego pola
        int minDist = Integer.MAX_VALUE; //najmniejsza odległość od pozycji

        for (int dx=-radius; dx<=radius; dx++) {  //iteracja po polach w promieniu
            for (int dy=-radius; dy<=radius; dy++) {
                Coord newPos = new Coord(position.x + dx, position.y + dy); //wspołrzedne kandydata
                if (world.inBounds(newPos.x, newPos.y)) {   //czy w granicach
                    Tile tile = world.getTile(newPos);  //pobranie pola
                    if (tile!=null && tile.getMapType()==MapType.CORAL && !world.isOccupied(newPos)) { //sprawdza czy to jest coral i czy ktoś tam jest
                        int dist = position.distance(newPos);  //odległość od pola coral
                        if (dist < minDist) {
                            minDist = dist;
                            nearestCoral = new Coord(newPos.x, newPos.y);
                        }
                    }
                }
            }
        }
        return nearestCoral; //zwaca najbliższe pole z coral
    }
}
