package movement;

import body.Animal;
import ocean.World;

public interface IFight {
    boolean canAttack(Animal other);
    boolean attack(Animal target, World world);
}

