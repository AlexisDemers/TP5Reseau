package tp5;

public class Carte {
    public static final int NOMBRE_DE_SORTES = 4;
    public static final int NOMBRE_DE_NOMBRES = 13;
    private int sorte;
    private int nombre;
    private ValeurCarte valeur;
    
    public Carte(int sorte, int nombre) throws CarteInvalidException{
        if(sorte < 4 && sorte >= 0 && nombre < 13 && nombre >= 0){
            this.sorte = sorte;
            this.nombre = nombre;
            this.valeur = ValeurCarte.values()[nombre];
        }
        else{
            throw new CarteInvalidException("INVALID Sorte/Nombre: " + sorte + "/" + nombre);
        }
    }
    
    public Carte(int carte){
        sorte = (int)Math.floor(carte/Carte.NOMBRE_DE_NOMBRES);
        nombre = carte%Carte.NOMBRE_DE_NOMBRES;
        this.valeur = ValeurCarte.values()[nombre];
    }
    
    public int getValeur(){
        System.out.println(this.valeur.getValeur() + " " + this.nombre);
        return valeur.getValeur();
    }
    
    public int getSorte(){
        return this.sorte;
    }
    
    public int getNombre(){
        return this.nombre;
    }
    
    public static boolean peutJouer(Carte c, int pts){
        return pts + c.getValeur() < 100;
    }
    
    public int toInt(){
        return (this.sorte*Carte.NOMBRE_DE_NOMBRES + this.nombre);
    }
}
