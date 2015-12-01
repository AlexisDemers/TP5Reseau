package sfad.tp5;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Paquet {
    public List<Carte> cartes;
    private int cartesRestantes;
    
    public Paquet(){
        try{
            cartes = new ArrayList<>();
            for(int i = 0; i < Carte.NOMBRE_DE_SORTES; i++){
                for(int j = 0; j < Carte.NOMBRE_DE_NOMBRES; j++){
                    cartes.add(new Carte(i, j));
                    cartesRestantes++;
                }
            }
        } catch(CarteInvalidException e){}
    }
    
    public void brasserPaquer(){
        if(cartes != null){
            Collections.shuffle(cartes);
        }
    }
    
    public Carte pigerCarte(){
        Carte carteRetour = null;
        if(resteCartes()){
            carteRetour = cartes.remove(0);
        }
        return carteRetour;
    }
    
    public boolean resteCartes(){
        return cartesRestantes > 0;
    }
    
    public int getCartesRestantes(){
        return cartesRestantes;
    }
}
