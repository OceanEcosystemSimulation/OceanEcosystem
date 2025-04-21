package ocean;


public class Fish extends Animal implements IEat, IMove {
    public Fish(Coord position) {
        super(position);
        this.maxAge = 100 + rand.nextInt(50);  //random max age - do ustawienia
        this.maxLoneliness = 40 + rand.nextInt(20); //random max loneliness - do ustawienia
    }

    public void update(World world) {
        age++;
        foodLevel--;
        loneliness++;

        if (foodLevel <= 0 || age > maxAge || loneliness > maxLoneliness) {
            alive = false;
            return; //zatrzymuje wykonywanie metody dla tego zwierzęcia, żadne dalsze działania, takie jak poruszanie się lub jedzenie, nie zostaną wykonane, ponieważ zwierzę jest martwe.
        }
        move(world); //wywołanie mechaniki ruchu

        Tile tile = world.getTile(position); //pobiera pole na którym znajduje się ryba
        if (tile != null && tile.hasFood() && canEat(tile)) { //sprawdza jest jedzenie i czy ryba może je zjeść
            eat(tile); //wywołanie mechaniki jedzenia
        }
    }

    @Override
    public void move(World world) {
        Coord newPos = position.randomAdjacent(world.getWidth(), world.getHeight());
        position = newPos; //update do nowych koordynatów
    }

    @Override
    public boolean canEat(Tile tile) {
        return tile.foodType == FoodType.PLANKTON || tile.foodType == FoodType.ALGAE;
    }

    @Override
    public void eat(Tile tile) { //przykładowe jak pisać
        switch (tile.foodType) { //sorry za switch case ale tak mi się to cholernie podoba zawsze że nie mogłam się oprzeć by nie użyć
            case PLANKTON -> foodLevel = Math.min(100, foodLevel + 10); //mniej daje
            case ALGAE -> foodLevel = Math.min(100, foodLevel + 20); //ryba lubi glony więc wiecej XD
        }
        tile.clearFood();
    }
}