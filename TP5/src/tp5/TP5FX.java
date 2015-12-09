/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tp5;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 *
 * @author AlexisDT
 */
public class TP5FX extends Application implements Initializable{
    @FXML Button btnConnect;
    @FXML Button btnFinTour;
    @FXML Button btnPiger;
    @FXML TextField txtJ1;
    @FXML TextField txtJ2;
    @FXML TextField txtJ3;
    @FXML TextField txtJ4;
    @FXML Label lblPoints;
    
    private static Table table;
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("TP5FXMLConnect.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
    }
            
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
    
    @FXML
    public void onConnect(){
        Stage stage = (Stage) btnConnect.getScene().getWindow();
        stage.hide();
        
        Parent root = null;
        
        try{
            root = FXMLLoader.load(getClass().getResource("TP5FXMLGame.fxml"));
        } catch(IOException e){}
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
        
        ArrayList<InetAddress> list = new ArrayList<>();
        try{
            if(!txtJ1.getText().equals("")){
                list.add(0, InetAddress.getByName(txtJ1.getText()));
            }
            if(!txtJ2.getText().equals("")){
                list.add(1, InetAddress.getByName(txtJ2.getText()));
            }
            if(!txtJ3.getText().equals("")){
                list.add(2, InetAddress.getByName(txtJ3.getText()));
            }
            if(!txtJ4.getText().equals("")){
                list.add(3, InetAddress.getByName(txtJ4.getText()));
            }
            table = new Table(this);
            table.joueurs = list;
            for (int i = 0; i < table.joueurs.size(); i++) {
                if (table.joueurs.get(i).getHostAddress().equals(InetAddress.getLocalHost().getHostAddress())) {
                    table.joueurLocal = new Joueur(i, table);
                }
            }
        } catch(UnknownHostException e){
        System.out.println("Unknown Host");}
    }
    
    @FXML
    private void pigerCarte(){
            System.out.println(table == null);
        if(btnPiger.getText().equals("Debuter")){
            btnPiger.setText("Piger");
            btnPiger.setDisable(true);
            table.joueurBrasseur = table.joueurLocal;
            table.debuterJeu();
        }else {
            
        }
    }
    
    @FXML
    private void finTour(){
        table.joueurBrasseur = table.joueurLocal;
        table.debuterJeu();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
