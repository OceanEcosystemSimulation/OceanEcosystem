package body;

public class AnimalEffectsManager {

    //wczesniej poison() w Animal
    static void applyPoisonEffect(Animal animal) {
        if (animal.getPoisonTicks() > 0) {
            animal.setHealth(animal.getHealth() - 5); // odejmuje 5HP co turę
            animal.setPoisonTicks(animal.getPoisonTicks() - 1); // dekrementuje licznik co turę
        }
    }

    //wczesniej w attack() w puffer
    public static void poisonTarget(Animal target) {
        target.setPoisonTicks(4); //ustawia na ile jest poisoned
        System.out.println(target.getName() + " id: " + target.getId() + " got poisoned by dying Puffer!");
    }



    //wczesniej updateSlowEffect() w Animal
    public static void updateInkEffect(Animal self) {
        if (self.getSlowCounter() > 0) {
            self.setSlowCounter(self.getSlowCounter() - 1);
        }
    }
}
