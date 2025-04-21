package ocean;

import java.util.List;

public class Shark extends Animal implements IMove, IFight, IEat {

    public Shark(Coord position) {
        super(position);
        this.maxAge = 150 + rand.nextInt(30); //do zmiany
        this.maxLoneliness = 70; //do zmiany
    }

    @Override
    public void update(World world) {
        age++;
        foodLevel--;
        loneliness++;

        if (foodLevel <= 0 || age > maxAge || loneliness > maxLoneliness) {
            alive = false;
            return;
        }

        move(world);

        List<Animal> nearby = world.getNearbyAnimals(position, 1); //pobiera liste zwierzat w otoczeniu rekina
        for (Animal a : nearby) {
            if (a != this && canAttack(a)) { //jeżeli zwierze nie jest nim samym to czy może zaatakować
                if (attack(a)) {
                    foodLevel = Math.min(100, foodLevel + 50); //boost jedzonka ale do zmiany
                    break;
                }
            }
        }
    }

    @Override
    public void move(World world) {
        position = position.randomAdjacent(world.getWidth(), world.getHeight());
    }

    @Override
    public boolean canEat(Tile tile) { //also przykładowe
        return foodLevel <= 30 &&
                (tile.foodType == FoodType.PLANKTON || tile.foodType == FoodType.ALGAE);
    }

    @Override
    public void eat(Tile tile) { //also przykładowe
        switch (tile.foodType) {
            case PLANKTON, ALGAE -> foodLevel = Math.min(100, foodLevel + 5); //np. że oba tyle samo dają
        }
        tile.clearFood();
    }


    @Override
    public boolean canAttack(Animal other) { //przykładowe że może atakować tylko Fish
        return other instanceof Fish;
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

