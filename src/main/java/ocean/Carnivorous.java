package ocean;

//abstract bo nie ma update
public abstract class Carnivorous extends Animal implements IFight {
    public Carnivorous(Coord position) {
        super(position);
    }



    @Override
    public boolean attack(Animal target) { //oblicza atak swój i defence celu ale to przykład więc do zmiany trch
        int attackerScore = getGenes().strength + getGenes().speed;
        int defenderScore = target.getGenes().strength + target.getGenes().speed;

        if (attackerScore >= defenderScore) {
            target.die();
            return true;
        } else {
            getGenes().strength = Math.max(getGenes().strength - 1, 1); //porażka i zmniejszenie siły - do zmiany
            return false;
        }
    }
}

