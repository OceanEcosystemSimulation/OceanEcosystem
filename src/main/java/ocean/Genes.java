package ocean;

//geny i ich mechanika (ogólnie wydaje mi się że latwo tak dodawać inne nowe jak trzeba)
public class Genes {
    private int strength;
    private int speed; //wartości max 5 kratek - zależy od tego ile się przesuwa/widzi
    private int fertility; //rozrodczość


    //geny potomstwa (trzeba przenieść maxAge i maxLoneliness też raczej)
    public static Genes inherit(Genes g1, Genes g2) {
        Genes g = new Genes();
        g.strength = mutate((g1.strength + g2.strength) / 2);
        g.speed = mutate((g1.speed + g2.speed) / 2);
        g.fertility = mutate((g1.fertility + g2.fertility) / 2);
        return g;
    }

    //losowa mutacja +-2 do genów
    private static int mutate(int value) {return value + (int)(Math.random() * 5 - 2);}

    public int getStrength() {return strength;}
    public int getSpeed() {return speed;}
    public int getFertility() {return fertility;}

    public void setStrength(int strength) {this.strength = strength;}
    public void setSpeed(int speed) {this.speed = speed;}
    public void setFertility(int fertility) {this.fertility = fertility;}
}

