package ocean;

//abstract bo nie ma update
public abstract class Carnivorous extends Animal implements IFight {
    public Carnivorous(Coord position) {
        super(position);
    }



    @Override
    public boolean attack(Animal target) { //oblicza atak swój i defence celu ale to przykład więc do zmiany trch
        int attackerScore = getGenes().getStrength() + getGenes().getSpeed();
        int defenderScore = target.getGenes().getStrength() + target.getGenes().getSpeed();

        if (attackerScore >= defenderScore) {
            target.die();
            return true;
        } else {
            getGenes().setStrength(Math.max(getGenes().getStrength() - 1, 1)); //porażka i zmniejszenie siły - do zmiany
            return false;
        }
    }
}

