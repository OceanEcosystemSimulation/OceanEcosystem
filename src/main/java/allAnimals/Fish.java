package allAnimals;

import ocean.*;

import static ocean.Coord.meetingAtMiddle;

public class Fish extends Herbivorous {
    public Fish(Coord position) {
        super(position, generateGenes(), 100 + rand.nextInt(50), 40 + rand.nextInt(20));
        //wartości maxAge i maxLoneliness do zmiany
        setName("Fish");
    }


    //do tworzenia genów w nowych - zakresy w losowych wartościah do zmiany
    //nie może potrzebować objektu by dzialac bo to ma tworzyć konstruktor (objekt) a nie byc uzywanym przez niego wiec static
    private static Genes generateGenes() {
        Genes g = new Genes();
        g.setStrength(5 + rand.nextInt(5));
        g.setSpeed(10 + rand.nextInt(10));
        g.setFertility(20 + rand.nextInt(10));
        return g;
    }

    public void update(World world) {
        processLifeCycle(world); //duperele o życiu
        if (!isAlive()) {return;}

        move(world); //wywołanie mechaniki ruchu

        tryToEat(world); //wywołanie mechaniki jedzenia
        tryToMate(world);
    }


    //sprawdzenie czy na obecnym kafelku znajduje się jedzenie
    private void tryToEat(World world) {
        Tile currentTile = world.getTile(getPosition()); //pobiera pole na którym znajduje się ryba
        if (currentTile!=null && currentTile.hasFood() && canEat(currentTile)) { //sprawdza czy jest jedzenie (na wszelki?) i czy ryba może je zjeść
            eat(currentTile); //wywołanie mechaniki jedzenia
        }
    }

    //szuka partnera
    private void tryToMate(World world) {
        if (isAlive()) {
            Animal mate = WorldSearch.nearestMate(world, this.getPosition(), this.getGenes().getSpeed(), this); //znajduje mate
            if (mate != null) {
                Coord target = meetingAtMiddle(world.getWidth(), world.getHeight(), this.getPosition(), mate.getPosition(), rand);
                //UWAGA!!!!!: ta metoda meetingAtMiddle jest popieprzona, coś mi się rozwaliło i robiłam cokolwiek by nie podkreślało już, ale nwm co się tu stało
                setPosition(target); //skok
            }
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
            setFoodLevel(getFoodLevel() + gain); //je
            System.out.println(this.getName() + " id: " + this.getId() + " eats " + tile.foodType);
            tile.clearFood();
        }
    }
}