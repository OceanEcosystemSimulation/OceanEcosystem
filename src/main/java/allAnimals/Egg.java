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
        super(position, genes);
        this.genes = genes;
        this.hatching = hatching;
    }

    @Override
    public void update(World world) {
        hatching--;
        if (hatching == 0) {
            Animal baby = giveBirth(getPosition(), mother, mother.getFatherDuringPregnancy());
            world.addAnimal(baby);
            world.getAnimals().remove(this);
        }
    }

    @Override
    public Animal giveBirth(Coord position, Animal parent1, Animal parent2) {
        return mother.giveBirth(position, parent1, parent2);
    }


    /*public Coord getPosition() {
        return position;
    }*/

    public Animal getMother() {
        return mother;
    }

    public void setMother(Animal mother) {
        this.mother = mother;
    }
}
