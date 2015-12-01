package tp5;

public class Trame {
    public static final int MESSAGE_DATA = 1;
    public static final int MESSAGE_RESPONSE = 2;
    public static final int TYPE_JEU = 2;
    private int seq;
    private int type= -1;
    private int source;
    private int destinataire;
    private int data;
    
    public Trame(String trameString){
        trameString = trameString.trim();
        if(trameString.length()>=3){
            seq = Integer.parseInt(""+trameString.charAt(0));
            type = Integer.parseInt(""+trameString.charAt(1));
            destinataire = Integer.parseInt(""+trameString.charAt(2));
            source = Integer.parseInt(""+trameString.charAt(3));
            data = Integer.parseInt(""+trameString.charAt(4));
        }
    }
    
    public Trame(int seq, int type, int destinataire, int source, int data){
        this.seq = seq;
        this.type = type;
        this.destinataire = destinataire;
        this.source = source;
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
    
    public int getDestinataire(){
        return this.destinataire;
    }
    
    public int getSource(){
        return this.source;
    }
    
    public void incSeq(){
        seq = ++seq%2;
    }
    
    @Override
    public String toString(){
        return seq + "" + type + "" + source + "" + destinataire + "" + data;
    }
}
