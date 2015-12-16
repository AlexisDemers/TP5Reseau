package tp5;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

public class Table {
    public static final int MAX_POINTS = 99;
    public Joueur joueurLocal;
    private Paquet paquet;
    public Joueur joueurBrasseur;
    public int joueurTour;
    public List<InetAddress> joueurs;
    public int points;
    public boolean jeuInverse = false;
    private int joueursRestant;
    private TP5FX vue;
    
    public Table(TP5FX vue){
        this.vue = vue;
        paquet = new Paquet();
    }
    
    public Carte pigerCarte(){
        return this.paquet.pigerCarte();
    }
    
    public void debuterJeu(){
        this.joueursRestant = joueurs.size();
        vue.btnPiger.setText("Piger");
        vue.btnPiger.setDisable(true);
        if(this.joueurLocal.equals(joueurBrasseur)){
            this.brasserPaquet();
            this.joueurTour = this.joueurLocal.getJoueurNo();
            this.nextJoueur();
            for(int j = 0; j < 3*this.getNbJoueurs(); j++){
                this.joueurLocal.jouer(null);
                nextJoueur();
                System.out.println("NOJ: " + this.getJoueurTour());
            }
            
            while(this.joueurLocal.getMain().size() < 3){
            }
        }
    }
    
    public void ecouteurBouton(){
        //TODO send source id, if carte image, substring id
        //joueurLocal.jouer();
    }
    
    public int getJoueurTour(){
        return joueurTour;
    }
    
    public void brasserPaquet(){
        paquet.brasserPaquer();
    }
    
    public void nextJoueur(){
        if(!jeuInverse){
            joueurTour = (joueurTour+1)%this.getNbJoueurs();
        } else {
            joueurTour = joueurTour == 0 ? this.getNbJoueurs() - 1 : joueurTour--;
        }
    }
    
    public int getNbJoueurs(){
        return joueurs.size();
    }
    
    public int joueursRestant(){
        return this.joueursRestant;
    }
    
    public void joueurPerdu(){
        this.joueursRestant--;
    }
    
    public void inverserJeu(){
        this.jeuInverse = !jeuInverse;
    }


}
