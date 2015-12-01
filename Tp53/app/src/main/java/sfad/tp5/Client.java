package sfad.tp5;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author 1261669
 */
public class Client extends Application implements Initializable{
    private static int SOCKET_PORT = 44444;
    
    private Stage stage;
    private DatagramSocket socket;
    private InetAddress IPAddress;
    private byte[][] buffer;
    private Trame trameSent;
    private Trame trameReceived;
    private ScheduledExecutorService timer;
    private BufferedOutputStream fromSocket;
    private BufferedInputStream fromFile;
    private int seq;
    private int ack;
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Vue.fxml"));
        Scene scene = new Scene(root);
        this.stage = stage;
        
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try{
            socket = new DatagramSocket(SOCKET_PORT);
        }catch(SocketException e){
            e.printStackTrace();
        }
        seq = 0;
        ack = 0;
        receiveData();
    }

    /*
    * Method called when the choose button is pressed
    * 
    * Opens the file chooser and verifies data, then starts the sending thread and
    * timer
    */
    public void fcOnClick(){
    }
    
    /*
    * Begins the listen thread on the socket
    */
    private void receiveData(){
        new Thread(()->{
            while(true){
                byte[] receiveData = new byte[BYTES_TO_READ + 2];
                try{
                    System.out.println("Waiting...");
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    socket.receive(receivePacket);
                    trameReceived = new Trame(new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength()));
                    IPAddress = receivePacket.getAddress();
                    if(trameReceived.getData().length > 0){
                        onReceive();
                    }
                } catch(IOException e){}
                 
            }
        }).start();
    }
    
    /*
    * Checks the current receivedTrame object for data.
    * Based on what type of data is received, it changes
    * the behavior and for most cases sends a new packet
    * to the sender on the socket.
    */
    private void onReceive(){
        try{
            switch(trameReceived.getType()){
                case Trame.MESSAGE_RESPONSE:
                    if(trameReceived.getSeq() != seq){
                        incSeq();
                        if(fromFile.available() > 0){
                            fromFile.read(buffer[seq]);
                            trameSent = new Trame(seq, Trame.MESSAGE_DATA, buffer[seq]);
                        }
                        send();
                    }
                    break;
                case Trame.MESSAGE_DATA:
                    if(trameReceived.getSeq() == ack){
                        Platform.runLater(()->lstMessages.getItems().add("System: " + trameReceived.getData().length + " bytes received"));
                        
                        incAck();
                        if(fromSocket != null){
                            fromSocket.write(trameReceived.getData());
                        }
                        trameSent = new Trame(ack, Trame.MESSAGE_RESPONSE, (trameReceived.getData().length + " bytes received").getBytes()); 
                        send();
                    }
                    break;
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }    
    
    /*
    * Send the current trameSent object on the socket
    */
    private void send(){
        try{
            DatagramPacket sendPacket = new DatagramPacket(trameSent.toString().getBytes(), trameSent.toString().getBytes().length, IPAddress, SOCKET_PORT);
            socket.send(sendPacket);
        } catch(IOException e){
            e.printStackTrace();
        }
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
    
    public void onClose(){
        System.exit(0);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
