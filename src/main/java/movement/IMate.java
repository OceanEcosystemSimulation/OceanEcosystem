package movement;

import body.Animal;
import extendedMechanics.Reproduction;
import ocean.World;
import ocean.WorldSearch;

public interface IMate {
    default void tryToMate(World world, Animal self) {
        int range = self.getGenes().getSpeed();
        Animal mate = WorldSearch.nearestMate(world, self.getPosition(), range, self);
        if (mate != null) {
            if (Reproduction.isDistance(self, mate)) { //jeżeli są w kratkach obok
                Reproduction.ReproductionProcess(self, mate); //mechanika reprodukcji
            } else {
                boolean move = Reproduction.moveToMate(self, mate, world);
                if (move && Reproduction.isDistance(self, mate)) { //czy się przesunął i na wszelki czy mate jest obok
                    Reproduction.ReproductionProcess(self, mate); //mechanika reprodukcji
                }
            }
        }
    }


}

