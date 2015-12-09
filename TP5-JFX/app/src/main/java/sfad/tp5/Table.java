package sfad.tp5;

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
    public boolean jeuInverse;
    private int joueursRestant;
    
    public Table(){
        paquet = new Paquet();
    }
    
    public Carte pigerCarte(){
        return this.paquet.pigerCarte();
    }
    
    public void debuterJeu(){
        this.joueurBrasseur = this.joueurLocal;
        this.joueursRestant = 4; // TODO : set based on number of actual players
        while(this.joueurBrasseur.getMain().size() < 3){
            this.joueurBrasseur.jouer(null);
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
            joueurTour = (joueurTour++)%this.getNbJoueurs();
        } else {
            joueurTour = joueurTour == 0 ? this.getNbJoueurs() : joueurTour--;
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
