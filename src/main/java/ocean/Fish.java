package ocean;

public class Fish extends Animal {
    public Fish(Coord position) {
        super(position); //przepisanie z Animal
        this.maxAge = 100 + rand.nextInt(50); //losowanie jakieś wartości
        this.maxLoneliness = 40 + rand.nextInt(20); //losowanie jakieś wartości
    }

    @Override
    public void update(World world) {
        age++;
        foodLevel--;
        loneliness++;

        //sprawdza czy martwa z głodu, samotności, wieku
        if (foodLevel <= 0 || age > maxAge || loneliness > maxLoneliness) {
            alive = false;
            return;
        }

        //proste poruszanie się - do zmiany napewno
        Coord newPos = position.randomAdjacent(world.getWidth(), world.getHeight()); //losowa pozycja
        Tile tile = world.getTile(newPos); //pole nowej pozycji

        if (tile != null && tile.food && canEat(tile)) { //jeśli jest jedzenie i jeśli może zjeść
            foodLevel = Math.min(100, foodLevel + 20); //foodLevel up chyba że więcej niż 100 to tylko 100
            tile.food = false; //pole już nie ma jedzenia
        }
        position = newPos;
    }

    @Override
    public boolean canEat(Tile tile) { //zwraca true jeśli jest jedzenie
        return tile.food;
    }

    @Override
    public boolean canAttack(Animal other) { //ryby nie atakują
        return false;
    }
}

