package tp5;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;

public class Joueur {
    private static final int SOCKET_PORT = 44444;
    public static final int JOUEUR_MULTICAST = 4;
    private List<Carte> main;
    private State etat;
    private final int joueurNo;
    private final Table table;
    private Trame trameReceived;
    private Trame trameSent;
    private InetAddress IPAddress;
    private ScheduledExecutorService timer;
    private DatagramSocket socket;
    private int seq;
    private int ack;
    private boolean multicast = false;
    
    public Joueur(int joueurNo, Table table){
        this.joueurNo = joueurNo;
        main = new ArrayList<>();
        etat = State.IDLE;
        this.table = table;
        try{
            socket = new DatagramSocket(SOCKET_PORT);
        }catch(SocketException e){
            e.printStackTrace();
        }
        seq = 0;
        ack = 0;
        debutEcouteur();
    }
    
    public void onReceive(){
        switch(trameReceived.getType()){
            case Trame.MESSAGE_CARTE_PASSEE:
                main.add(new Carte(trameReceived.getData()));
                switch(this.etat){
                    case ASSIS:
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
                break;
            case Trame.MESSAGE_CARTE_JOUEE:
                Carte carteJouee = new Carte(trameReceived.getData());
                
                table.points += carteJouee.getValeur();
                
                if(carteJouee.getNombre() == ValeurCarte.ROI.getId()){
                    table.points = 99;
                } else if (carteJouee.getNombre() == ValeurCarte.VALET.getId()) {
                    table.inverserJeu();
                } 
                if(table.points > Table.MAX_POINTS){
                    for(InetAddress i : table.joueurs){
                        if(i.equals(IPAddress)){
                            table.joueurPerdu();
                        }
                    }
                }
                
                if (table.getJoueurTour() == this.joueurNo){
                    if(this.main.isEmpty() || !peutJouer()){
                        this.perdre();
                    }
                }
                break;
            case Trame.MESSAGE_NEXT_JOUEUR:
                table.nextJoueur();
                    if(table.joueurTour == this.joueurNo){
                        //TODO Attente jeu
                    }
            }
        }   
    
    public void jouer(String sourceId){
        switch(this.etat){
            case ASSIS:
                if(this.isBrasseur()){
                    Carte c = table.pigerCarte();
                    trameSent = new Trame(this.seq, Trame.MESSAGE_CARTE_PASSEE, c.toInt());
                    multicast = false;
                    //startTimer();
                    this.send();
                    table.nextJoueur();
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
                    } else if (carteJouee.getNombre() == ValeurCarte.VALET.getId()) {
                        table.inverserJeu();
                    }
                    
                    trameSent = new Trame(seq, Trame.MESSAGE_CARTE_JOUEE, carteJouee.toInt());
                    multicast = true;
                    this.send();
                    if(table.points > Table.MAX_POINTS){
                        this.perdre();
                    }
                }
        }
    }
    
    /*
    * Starts the timer to repeatedly send the trameSent object
    * to secure that the packet was properly received
    */
    private void startTimer(){
        timer = Executors.newSingleThreadScheduledExecutor();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try{
                    send();
                }catch(Exception e){}
            }
        }, 1, 1, TimeUnit.SECONDS);
    }
    
    /*
    * Shutsdown the timer
    */
    private void stopTimer(){
       timer.shutdown();
    }
    
    /*
    * Switches between 0 and 1 for the sequence number
    */
    private void incSeq(){
        seq = (++seq)%2;
    }
    
    /*
    * Switches between 0 and 1 for the acknowledgement number
    */
    private void incAck(){
        ack = (++ack)%2;
    }
    
    private void send(){
        if(trameSent != null){
            if(multicast){
                for(InetAddress i : table.joueurs){
                    byte[] message = trameSent.toString().getBytes();
                    DatagramPacket p = new DatagramPacket(message, message.length, i, SOCKET_PORT);
                    try{
                        socket.send(p);
                    } catch(IOException e){}
                }
                
            }else {
                byte[] message = trameSent.toString().getBytes();
                DatagramPacket p = new DatagramPacket(message, message.length, table.joueurs.get(table.getJoueurTour()), SOCKET_PORT);
                try{
                    socket.send(p);
                } catch(IOException e){}
            }
        }
    }
    
    public void debutEcouteur(){
         new Thread(()->{
            while(table.getNbJoueurs() > 1){
                byte[] receiveData = new byte[3];
                try{
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    socket.receive(receivePacket);
                    trameReceived = new Trame(new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength()));
                    IPAddress = receivePacket.getAddress();
                    this.onReceive();
                } catch(IOException e){}
            }
            //TODO afficher gagnant
        }).start();
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
    
    public List<Carte> getMain(){
        return this.main;
    }
}
