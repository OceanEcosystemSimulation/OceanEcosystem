package ocean;

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
                    if (tile != null && tile.hasFood()) { //sprawdza czy jest na nim jedzenie
                        int dist = (int) position.distance(candidate); //rzutowanie double z euklidesa na int
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
            if (preyTile!=null && preyTile.type!=MapType.CORAL && predator.canAttack(animal)) { //sprawdzenie czy może zaatakować i czy to nie rafa
                int dist = (int) position.distance(animal.getPosition());
                if (dist < minDist) { //jeśli bliżej to bierze
                    minDist = dist;
                    nearestPrey = animal;
                }
            }
        }
        return nearestPrey!=null ? nearestPrey.getPosition() : null; //zwraca współrzędne ofiary lub null jak jej nie ma
    }


    //znajduje zwierze w pobliżu - prawdopodobnie mozna to połączyć z nearestPrey <-- do zmiany póżniej moze
    public static Animal nearestMate(World world, Coord position, int radius, Animal self) {
        Animal nearestMate = null; //zmenna z najblizszym mate
        int minDist = Integer.MAX_VALUE; //najmniejsza odległość od pozycji

        for (Animal animal : world.getNearbyAnimals(position, radius)) { //iteracja po liście zwierząt w promieniu
            if (animal != self && animal.isAlive() && animal.getName().equals(self.getName())) { //porównywanie gatunków
                int dist = (int) position.distance(animal.getPosition()); //odległość ich
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
        int minDist = Integer.MAX_VALUE;

        for (int dx=-radius; dx<=radius; dx++) {
            for (int dy=-radius; dy<=radius; dy++) {
                Coord newPos = new Coord(position.x + dx, position.y + dy);
                if (world.inBounds(newPos.x, newPos.y)) {
                    Tile tile = world.getTile(new Coord(newPos.x, newPos.y));
                    if (tile!=null && tile.type==MapType.CORAL) { //sprawdza czy to jest coral
                        int dist = (int) position.distance(newPos);
                        if (dist < minDist) {
                            minDist = dist;
                            nearestCoral = new Coord(newPos.x, newPos.y);
                        }
                    }
                }
            }
        }
        return nearestCoral;
    }
}
