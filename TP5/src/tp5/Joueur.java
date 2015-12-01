package tp5;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;

public class Joueur {
    public static final int JOUEUR_MULTICAST = 4;
    private List<Carte> main;
    private State etat;
    private final int joueurNo;
    private final Table table;
    
    public Joueur(int joueurNo, Table table){
        this.joueurNo = joueurNo;
        main = new ArrayList<>();
        etat = State.IDLE;
        this.table = table;
    }
    
    public void onReceive(Trame trame){
        if(trame.getType() == Trame.TYPE_JEU){
            switch(this.etat){
                case ASSIS:
                    main.add(new Carte(trame.getData()));
                    if(main.size() == 3){
                        etat = State.JEU;
                    }
                    break;
                case JEU:
                    if(table.getNbJoueurs() == 1){
                        this.gagner();
                    } else if (table.getJoueurTour() == this.joueurNo){
                        if(this.main.isEmpty() || !peutJouer()){
                            this.perdre();
                        }
                    } else {
                        //set vars ton tour
                    }
                    break;
            }
        }   
    }
    
    public void jouer(String sourceId){
        switch(this.etat){
            case ASSIS:
                if(this.isBrasseur()){
                    //passercarte
                    if(this.main.size() == 3){
                        this.etat = State.JEU;
                    }
                }
                break;
            case JEU:
                if(sourceId.equals("btnPiger")){
                    
                } else if(sourceId.equals("btnFinTour")){
                    
                } else {
                    Carte carteJouee = main.get(Integer.parseInt(sourceId));
                    table.points += carteJouee.getValeur();
                    if(carteJouee.getNombre() == ValeurCarte.ROI.getId()){
                        table.points = 99;
                    }
                    //TODO JACK
                    
                    if(table.points > Table.MAX_POINTS){
                        this.perdre();
                    }
                }
        }
    }
    
    public void perdre(){
        this.etat = State.PERDANT;
    }
    
    public void gagner(){
        this.etat = State.GAGNANT;
    }
    
    public int getJoueurNo(){
        return this.joueurNo;
    }
    
    public State getState(){
        return this.etat;
    }
    
    public void setState(State etat){
        this.etat = etat;
    }
    
    public boolean isBrasseur(){
        return this.joueurNo == 0;
    }
    
    public boolean peutJouer(){
        boolean peutJouer = false;
        for (Carte c : main){
            if(Carte.peutJouer(c, table.points)){
                peutJouer = true;
                break;
            }
        }
        return peutJouer;
    }
}
