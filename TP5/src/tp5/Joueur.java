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

public class Joueur {
    private static final int SOCKET_PORT = 44446;
    public static final int JOUEUR_MULTICAST = 4;
    private ArrayList<Carte> main;
    private State etat;
    private final int joueurNo;
    private final Table table;
    private Trame trameReceived;
    private DatagramPacket receivePacket;
    public Trame trameSent;
    private InetAddress IPAddress;
    private ScheduledExecutorService timer;
    private DatagramSocket socket;
    public int[] seq = new int[4];
    public int[] ack = new int[4];
    public boolean multicast = false;
    
    public Joueur(int joueurNo, Table table){
        this.joueurNo = joueurNo;
        main = new ArrayList<>();
        etat = State.IDLE;
        this.table = table;
        try{
            socket = new DatagramSocket(SOCKET_PORT);
            Runtime.getRuntime().addShutdownHook(new Thread(){public void run(){
                socket.close();
            }});
        }catch(SocketException e){
            e.printStackTrace();
        }
        for(int i = 0 ; i < table.joueurs.size(); i++){
            seq[i] = 0;
            ack[i] = 0;
        }
        debutEcouteur();
    }
    
    public void onReceive(){
                    System.out.println(trameReceived.toString());
        switch(trameReceived.getType()){
            case Trame.MESSAGE_CARTE_PASSEE:
                main.add(new Carte(trameReceived.getData()));
                table.recevoirCarte(new Carte(trameReceived.getData()));
                switch(this.etat){
                    case ASSIS:
                        if(main.size() == 3){
                            etat = State.JEU;
                        }
                        break;
                    case JEU:
                        break;
                   }
                   //this.incAck(table.getJoueurTour());
                   //trameSent = new Trame(this.ack[table.getJoueurTour()], Trame.MESSAGE_CONFIRMATION, 0);
                break;
            case Trame.MESSAGE_CARTE_JOUEE:
                Carte carteJouee = new Carte(trameReceived.getData());
                table.points += carteJouee.getValeur();
                if(carteJouee.getNombre() == ValeurCarte.ROI.getId()){
                    table.points = 99;
                }
                if(carteJouee.getNombre() == ValeurCarte.VALET.getId()) {
                    table.inverserJeu();
                } 
                table.updatePointage();
                
                table.carteJouee(carteJouee);
                
                if(table.points > Table.MAX_POINTS){
                    for(InetAddress i : table.joueurs){
                        if(i.equals(IPAddress)){
                            table.joueurPerdu();
                        }
                    }
                }                
                break;
            case Trame.MESSAGE_NEXT_JOUEUR:
                    table.nextJoueur();
                break;
            case Trame.MESSAGE_BRASSEUR:
                    table.joueurBrasseur = trameReceived.getData();
                    this.setState(State.ASSIS);
                    table.debuterJeu();
                    break;
            case Trame.MESSAGE_JOUEUR_NO:
                table.setJoueurTour(trameReceived.getData());
                break;
            case Trame.MESSAGE_DEMANDE_CARTE:
                trameSent = new Trame(this.seq[trameReceived.getData()], Trame.MESSAGE_CARTE_PASSEE, table.pigerCarte().toInt());
                multicast = false;
                send(trameReceived.getData());
                break;
            case Trame.MESSAGE_JOUEUR_PERDANT:
                table.joueurs.set(trameReceived.getData(), null);
                if(trameReceived.getData() != this.joueurNo && table.getNbJoueurs() == 1){
                    this.gagner();
                }
                break;
        }


        }   
    
    public void jouer(String sourceId){
        switch(this.etat){
            case ASSIS:
                if(this.isBrasseur()){
                    Carte c = table.pigerCarte();
                    trameSent = new Trame(this.seq[table.getJoueurTour()], Trame.MESSAGE_CARTE_PASSEE, c.toInt());
                    multicast = false;
                    //startTimer();
                    this.send(table.joueurTour);
                    if(this.main.size() == 3){
                        this.etat = State.JEU;
                    }
                }
                break;
            case JEU:
                if(sourceId.equals("btnPiger")){
                    if(!(table.points > Table.MAX_POINTS)){
                        trameSent = new Trame(seq[table.joueurBrasseur], Trame.MESSAGE_DEMANDE_CARTE, this.joueurNo);
                        multicast = false;
                        send(table.joueurBrasseur);
                    }
                } else if(sourceId.equals("btnFinTour")){
                    trameSent = new Trame(seq[table.joueurBrasseur], Trame.MESSAGE_NEXT_JOUEUR, 0);
                    multicast = true;   
                    send(0);
                    if(table.points > Table.MAX_POINTS){
                        this.perdre();
                        trameSent = new Trame(this.seq[table.getJoueurTour()], Trame.MESSAGE_JOUEUR_PERDANT, this.joueurNo);
                        multicast = true;
                        send(0);
                    }
                } else {
                    Carte carteJouee = main.remove(Integer.parseInt(sourceId));
                    trameSent = new Trame(seq[table.getJoueurTour()], Trame.MESSAGE_CARTE_JOUEE, carteJouee.toInt());
                    multicast = true;
                    this.send(0);
                    table.refreshCartes();
                }
        }
    }
    
    public void debutJeu(){
        if(table.joueurBrasseur == this.joueurNo){
            trameSent = new Trame(seq[table.getJoueurTour()], Trame.MESSAGE_BRASSEUR, this.joueurNo);
            multicast = true;
            send(0);
        } 
    }
    
    public void setJoueurTour(int joueur){
        trameSent = new Trame(seq[table.getJoueurTour()], Trame.MESSAGE_JOUEUR_NO, joueur);
        multicast = true;
        send(0);
    }
    
    /*
    * Starts the timer to repeatedly send the trameSent object
    * to secure that the packet was properly received
    */
    /*private void startTimer(){
        timer = Executors.newSingleThreadScheduledExecutor();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try{
                    send();
                }catch(Exception e){}
            }
        }, 1, 1, TimeUnit.SECONDS);
    }*/
    
    /*
    * Shutsdown the timer
    */
    /*private void stopTimer(){
       timer.shutdown();
    }*/
    
    /*
    * Switches between 0 and 1 for the sequence number
    */
    private void incSeq(int joueur){
        seq[joueur] = (++seq[joueur])%2;
    }
    
    /*
    * Switches between 0 and 1 for the acknowledgement number
    */
    private void incAck(int joueur){
        ack[joueur] = (++ack[joueur])%2;
    }
    
    public void send(int joueur){
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
                DatagramPacket p = new DatagramPacket(message, message.length, table.joueurs.get(joueur), SOCKET_PORT);
                try{
                    socket.send(p);
                } catch(IOException e){}
            }
        }
    }
    
    public void debutEcouteur(){
         new Thread(new Runnable() {
             @Override
             public void run() {
                 while(table.getNbJoueurs() > 1){
                     byte[] receiveData = new byte[4];
                     try{
                        receivePacket = new DatagramPacket(receiveData, receiveData.length);
                        socket.receive(receivePacket);
                        trameReceived = new Trame(new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength()));
                        IPAddress = receivePacket.getAddress();
                        onReceive();
                     } catch(IOException e){}
                     //TODO afficher gagnant
                 }
             }
         }).start();
    }
    
    public void perdre(){
        this.etat = State.PERDANT;
        System.out.println("PERDANT");
    }
    
    public void gagner(){
        this.etat = State.GAGNANT;
        System.out.println("GANGNANTNTN");
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
        return this.joueurNo == table.joueurBrasseur;
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
    
    public ArrayList<Carte> getMain(){
        return this.main;
    }
}
