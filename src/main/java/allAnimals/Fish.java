package allAnimals;

import ocean.*;

public class Fish extends Herbivorous {
    public Fish(Coord position) {
        super(position, generateGenes(), 100 + rand.nextInt(50), 40 + rand.nextInt(20));
        //wartości maxAge i maxLoneliness do zmiany
        setName("Fish");
    }

    public Fish(Coord position, Animal parent1, Animal parent2) {
        super(position, parent1, parent2);  //konstruktor dziecka
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