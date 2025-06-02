package body;

import ocean.SimulationStatsManager;

//obsługa efektów - poison/ink
public class AnimalEffectsManager {
    //aktualizacja efektu poison
    static void applyPoisonEffect(Animal animal) {
        if (animal.getPoisonTicks() > 0) {
            animal.setHealth(animal.getHealth() - 5); // odejmuje 5HP co turę
            animal.setPoisonTicks(animal.getPoisonTicks() - 1); // dekrementuje licznik co turę
        }
    }


    //zatruwa zwierze
    public static void poisonTarget(Animal target) {
        target.setPoisonTicks(4); //ustawia na ile jest poisoned
        SimulationStatsManager.writeToFile(target.getName() + " id: " + target.getId() + " is poisoned");
    }


    //aktualizacja ink
    public static void updateInkEffect(Animal self) {
        if (self.getSlowCounter() > 0) {
            self.setSlowCounter(self.getSlowCounter() - 1);
        }
    }
}
