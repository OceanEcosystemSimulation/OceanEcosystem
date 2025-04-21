package ocean;

public class Fish extends Herbivorous {
    public Fish(Coord position) {
        super(position);
        this.maxAge = 100 + rand.nextInt(50);  //random max age - do ustawienia
        this.maxLoneliness = 40 + rand.nextInt(20); //random max loneliness - do ustawienia
    }

    public void update(World world) {
        processLifeCycle(); //duperele o życiu
        if (!alive) {return;}

        move(world); //wywołanie mechaniki ruchu

        Tile tile = world.getTile(position); //pobiera pole na którym znajduje się ryba
        if (tile != null && tile.hasFood() && canEat(tile)) { //sprawdza jest jedzenie i czy ryba może je zjeść
            eat(tile); //wywołanie mechaniki jedzenia
        }
    }


    //zjada o ile nie byłoby ponad 100 napchane
    @Override
    public void eat(Tile tile) { //przykładowe jak pisać
        //sorry za switch case ale tak mi się to cholernie podoba zawsze że nie mogłam się oprzeć by nie użyć
        //troche dziwne to ale wydaje mi się że ładne czytelne :>
        int gain = switch (tile.foodType) {
            case PLANKTON -> 10;
            case ALGAE -> 15;
            default -> 0; //NONE
        };
        if (foodLevel+gain <= 100){
            tile.clearFood();
        }
    }




}