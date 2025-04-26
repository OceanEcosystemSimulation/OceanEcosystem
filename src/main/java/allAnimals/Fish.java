package allAnimals;

import ocean.Coord;
import ocean.Herbivorous;
import ocean.Tile;
import ocean.World;

public class Fish extends Herbivorous {
    public Fish(Coord position) {
        super(position);
        setMaxAge(100 + rand.nextInt(50)); //random max age - do ustawienia
        setMaxLoneliness(40 + rand.nextInt(20)); //random max loneliness - do ustawienia
    }

    public void update(World world) {
        processLifeCycle(); //duperele o życiu
        if (!isAlive()) {return;}

        move(world); //wywołanie mechaniki ruchu

        Tile tile = world.getTile(getPosition()); //pobiera pole na którym znajduje się ryba
        if (tile != null && tile.hasFood() && canEat(tile)) { //sprawdza jest jedzenie i czy ryba może je zjeść
            eat(tile); //wywołanie mechaniki jedzenia
        }
    }


    //zjada o ile nie byłoby ponad 100 napchane
    @Override
    public void eat(Tile tile) { //przykładowe jak pisać
        int gain = switch (tile.foodType) {
            case PLANKTON -> 10;
            case ALGAE -> 15;
            default -> 0; //NONE
        };
        if (getAge()+gain <= 100){
            setFoodLevel(getFoodLevel() + gain); //omnomnom ryby
            tile.clearFood();
        }
    }




}