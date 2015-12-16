package tp5;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;
import javafx.application.Platform;
import javafx.scene.image.Image;

public class Table {
    public static final int MAX_POINTS = 99;
    public Joueur joueurLocal;
    private Paquet paquet;
    public Joueur joueurBrasseur;
    public int joueurTour;
    public List<InetAddress> joueurs;
    public int points;
    public boolean jeuInverse = false;
    public int joueursRestant;
    private TP5FX vue;
    
    public Table(TP5FX vue){
        this.vue = vue;
        paquet = new Paquet();
    }
    
    public Carte pigerCarte(){
        return this.paquet.pigerCarte();
    }
    
    public void debuterJeu(){
        Platform.runLater(()->{vue.btnPiger.setText("Piger");});
        Platform.runLater(()->{vue.btnPiger.setDisable(true);});
            System.out.println("ASFASD " + this.getNbJoueurs());
        if(this.joueurLocal.equals(joueurBrasseur)){
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
            Platform.runLater(()->{
                vue.imgC1.setDisable(false);
                vue.imgC2.setDisable(false);
                vue.imgC3.setDisable(false);
            });
        }
    }
    
    public void recevoirCarte(Carte c){
            Platform.runLater(()->{
                File file = new File("../res/" + c.toInt() + ".png");
                if(vue.imgC1.getImage() == null)
                    vue.imgC1.setImage(new Image(file.toURI().toString()));
                else if(vue.imgC2.getImage() == null)
                    vue.imgC2.setImage(new Image(file.toURI().toString()));
                else if(vue.imgC3.getImage() == null)
                    vue.imgC3.setImage(new Image(file.toURI().toString()));
            });
    }
}
