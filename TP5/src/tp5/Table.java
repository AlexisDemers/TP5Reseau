package tp5;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.scene.image.Image;

public class Table {
    public static final int MAX_POINTS = 99;
    public Joueur joueurLocal;
    private Paquet paquet;
    public int joueurBrasseur;
    public int joueurTour;
    public List<InetAddress> joueurs;
    public int points;
    public boolean jeuInverse = false;
    public int joueursRestant;
    private TP5FX vue;
    private boolean partieCommencee = false;
    public boolean tourJoue = false;
    
    public Table(TP5FX vue){
        this.vue = vue;
        paquet = new Paquet();
    }
    
    public Carte pigerCarte(){
        return this.paquet.pigerCarte();
    }
    
    public void debuterJeu(){
        if(!partieCommencee){
            partieCommencee = true;
            Platform.runLater(()->{
                vue.btnPiger.setText("Piger");
                vue.btnPiger.setDisable(true);
            });
            if(this.joueurLocal.getJoueurNo() == joueurBrasseur){
                this.brasserPaquet();
                this.joueurTour = this.joueurLocal.getJoueurNo();
                this.nextJoueur();
                for(int j = 0; j < 3*this.getNbJoueurs(); j++){
                    this.joueurLocal.jouer(null);
                    nextJoueur();
                }
                this.joueurLocal.setJoueurTour(joueurTour);
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
    
    public void setJoueurTour(int joueur){
        this.joueurTour = joueur;
        if(joueur == joueurLocal.getJoueurNo()){
            this.tourJoue = false;
        }
    }
    
    public void recevoirCarte(Carte c){
            Platform.runLater(()->{
                if(vue.imgC1.getImage() == null){
                    vue.imgC1.setImage(new Image("/tp5/res/c" + c.toInt() + ".png"));
                    vue.imgC1.setVisible(true);
                }
                else if(vue.imgC2.getImage() == null){
                    vue.imgC2.setImage(new Image("/tp5/res/c" + c.toInt() + ".png"));
                    vue.imgC2.setVisible(true);
                }
                else if(vue.imgC3.getImage() == null){
                    vue.imgC3.setImage(new Image("/tp5/res/c" + c.toInt() + ".png"));
                    vue.imgC3.setVisible(true);
                }
            });
    }
    
    public void refreshCartes(){
        ArrayList<Carte> main = joueurLocal.getMain();
        vue.imgC1.setImage(null);
        vue.imgC2.setImage(null);
        vue.imgC3.setImage(null);
        
        for(Carte c : main){
            recevoirCarte(c);
        }
    }
    
    public void carteJouee(Carte c){
        vue.imgDerniereCarte.setImage(new Image("/tp5/res/c" + c.toInt() + ".png"));
    }
    
    public void updatePointage(){
        Platform.runLater(()->vue.lblPoints.setText(""+points));
    }
}
