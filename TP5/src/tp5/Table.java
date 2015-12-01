package tp5;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

public class Table {
    private static final int SOCKET_PORT = 44444;
    public static final int MAX_POINTS = 99;
    private Joueur joueurLocal;
    private Trame trameReceived;
    private Trame trameSent;
    private InetAddress IPAddress;
    private DatagramSocket socket;
    private Paquet paquet;
    public int joueurTour;
    public List<InetAddress> joueurs;
    public int points;
    public boolean jeuInverse;
    
    public Table(){
        paquet = new Paquet();
        joueurLocal = new Joueur(0, this/*TODO toFix*/);
    }
    
    public void debuterJeu(){
        
    }
    
    public void debutEcouteur(){
         new Thread(()->{
            while(this.getNbJoueurs() > 1){
                byte[] receiveData = new byte[3];
                try{
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    socket.receive(receivePacket);
                    trameReceived = new Trame(new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength()));
                    IPAddress = receivePacket.getAddress();
                    if(trameReceived.getDestinataire() == joueurLocal.getJoueurNo() 
                            || trameReceived.getDestinataire() == Joueur.JOUEUR_MULTICAST){
                        joueurLocal.onReceive(trameReceived);
                    }
                } catch(IOException e){}
            }
            //TODO afficher gagnant
        }).start();
    }
    
    public void ecouteurBouton(){
        //TODO send source id, if carte image, substring id
        joueurLocal.jouer();
        System.out.println("LOL");
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
    
    public void setJeuInverse(boolean jeuInverse){
        this.jeuInverse = jeuInverse;
    }
    
}
