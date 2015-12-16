package tp5;

public class Trame {
    public static final int MESSAGE_CONFIRMATION = 1;
    public static final int MESSAGE_CARTE_PASSEE = 2;
    public static final int MESSAGE_CARTE_JOUEE = 3;
    public static final int MESSAGE_NEXT_JOUEUR = 4;
    public static final int MESSAGE_BRASSEUR = 5;
    public static final int MESSAGE_JOUEUR_NO = 6;
    public static final int MESSAGE_DEMANDE_CARTE = 7;
    public static final int MESSAGE_JOUEUR_PERDANT = 8;
    private int seq;
    private int type= -1;
    private int data;
    
    public Trame(String trameString){
        trameString = trameString.trim();
        if(trameString.length()>=3){
            seq = Integer.parseInt(""+trameString.charAt(0));
            type = Integer.parseInt(""+trameString.charAt(1));
            data = Integer.parseInt(""+trameString.substring(2));
        }
    }
    
    public Trame(int seq, int type, int data){
        this.seq = seq;
        this.type = type;
        this.data = data;
    }
    
    public int getSeq(){
        return this.seq;
    }
    
    public int getType(){
        return this.type;
    }
    
    public int getData(){
        return this.data;
    }
    
    public void incSeq(){
        seq = ++seq%2;
    }
    
    @Override
    public String toString(){
        return seq + "" + type + "" + data;
    }
}
