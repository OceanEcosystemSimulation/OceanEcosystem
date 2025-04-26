package ocean;

public interface IFight {
    boolean canAttack(Animal other);
    boolean attack(Animal target, World world);
}

