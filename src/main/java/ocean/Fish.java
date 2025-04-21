package ocean;

//zostawiam IMove jakby trzeba było dać różne rodzaje chodzenia dla nich, jak coś to się przeniesie też do herbivorous
public class Fish extends Herbivorous implements IMove {
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


}