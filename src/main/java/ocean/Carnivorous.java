package ocean;

//abstract bo nie ma update
public abstract class Carnivorous extends Animal implements IFight {
    public Carnivorous(Coord position) {
        super(position);
    }



    @Override
    public boolean attack(Animal target) { //oblicza atak swój i defence celu ale to przykład więc do zmiany trch
        int attackerScore = genes.strength + genes.speed;
        int defenderScore = target.genes.strength + target.genes.speed;

        if (attackerScore >= defenderScore) {
            target.alive = false;
            return true;
        } else {
            genes.strength = Math.max(genes.strength - 1, 1); //porażka i zmniejszenie siły - do zmiany
            return false;
        }
    }
}

