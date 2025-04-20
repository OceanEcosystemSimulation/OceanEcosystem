package ocean;

//geny i ich mechanika (ogólnie wydaje mi się że latwo tak dodawać inne nowe jak trzeba)
public class Genes {
    public int strength;
    public int speed;
    public int fertility; //rozrodczość jakby ktoś nie wiedział

    //losowe geny
    //ogólnie najpierw tak dałam na szybko ale trzeba zmienić bo to dla wszytskich startowych
    //a mamy mieć inne dla każdego rodzaju
    public static Genes randomGenes() {
        Genes g = new Genes();
        g.strength = 5 + (int)(Math.random() * 20);
        g.speed = 5 + (int)(Math.random() * 20);
        g.fertility = 5 + (int)(Math.random() * 20);
        return g;
    }

    //geny potomstwa (trzeba przenieść maxAge i maxLoneliness też raczej)
    public static Genes inherit(Genes g1, Genes g2) {
        Genes g = new Genes();
        g.strength = mutate((g1.strength + g2.strength) / 2);
        g.speed = mutate((g1.speed + g2.speed) / 2);
        g.fertility = mutate((g1.fertility + g2.fertility) / 2);
        return g;
    }

    //losowa mutacja +-2 do genów
    private static int mutate(int value) {
        return value + (int)(Math.random() * 5 - 2);
    }
}

