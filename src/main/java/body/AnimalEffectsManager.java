package body;

import ocean.SimulationStatsManager;

//obsługa efektów - poison/ink
/**
 * Manages the status of effects on animals like poison and ink.
 * Handles updating this effect durations and applying relevant consequences.
 */
public class AnimalEffectsManager {
    /**
     * Applies the poison effect to the given animal.
     * If the animal is poisoned (poisonTicks more than 0), it loses 5 HP per turn and the poison duration is reduced by one tick.
     * @param animal The animal affected by poison.
     */
    static void applyPoisonEffect(Animal animal) {
        if (animal.getPoisonTicks() > 0) {
            animal.setHealth(animal.getHealth() - 5); // odejmuje 5HP co turę
            animal.setPoisonTicks(animal.getPoisonTicks() - 1); // dekrementuje licznik co turę
        }
    }


    /**
     * Inflicts poison status on a targeted animal setting its poison duration.
     * Also logs the poisoning event in the simulation statistics.
     * @param target The animal that will be poisoned.
     */
    public static void poisonTarget(Animal target) {
        target.setPoisonTicks(4); //ustawia na ile jest poisoned
        SimulationStatsManager.writeToFile(target.getName() + " id: " + target.getId() + " is poisoned");
    }


    /**
     * Updates the ink effect on the given animal.
     * If the animal has a slow counter active it's reduced by one per turn.
     * @param self The animal affected by ink.
     */
    public static void updateInkEffect(Animal self) {
        if (self.getSlowCounter() > 0) {
            self.setSlowCounter(self.getSlowCounter() - 1);
        }
    }
}
