package tp5;

public enum ValeurCarte {
    AS(0, 1),
    DEUX(1, 2),
    TROIS(2, 3),
    QUATRE(3, 4),
    CINQ(4, 5),
    SIX(5, 6),
    SEPT(6, 7),
    HUIT(7, 8),
    NEUF(8, 0),
    DIX(9, -10),
    VALET(10, 0),
    DAME(11, 10),
    ROI(12, 0);
    
    private final int id;
    private final int valeur;
    ValeurCarte(int id, int valeur){
        this.id = id;
        this.valeur = valeur;
    }

    public int getValeur(){
        return this.valeur;
    }

    public int getId(){
        return this.id;
    }
}
