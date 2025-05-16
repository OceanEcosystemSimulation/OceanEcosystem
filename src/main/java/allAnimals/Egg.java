package allAnimals;

import body.Animal;
import body.Genes;
import map.Coord;
import ocean.World;

public class Egg extends Animal {
    private final Genes genes;
    private int hatching;
    private Animal mother;

    public Egg(Coord position, Genes genes, int hatching) {
        super(position, genes, 0, 0, 100);
        this.genes = genes;
        this.hatching = hatching;
    }

    @Override
    public void update(World world) {
        hatching--;
        if (hatching == 0) {
            Animal baby = this.getMother().giveBirth(getPosition(), genes);
            world.addAnimal(baby);
            world.getAnimals().remove(this);
        }
    }

    @Override
    public Animal giveBirth(Coord position, Genes genes) {
        return mother.giveBirth(position, genes);
    }

    /*public Coord getPosition() {
        return position;
    }*/

    public void setMother(Animal mother) {
        this.mother = mother;
    }

    public Animal getMother() {
        return mother;
    }
}
