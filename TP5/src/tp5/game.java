package tp5;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class game extends AppCompatActivity {

    public Button send;
    public Button piger;
    public Button finTour;
    public EditText ip1;
    public EditText ip2;
    public EditText ip3;
    public EditText ip4;
    public String ip;
    Boolean first = true;
    public Table table;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip);

        ip1 = (EditText) this.findViewById(R.id.ip1);
        ip2 = (EditText) this.findViewById(R.id.ip2);
        ip3 = (EditText) this.findViewById(R.id.ip3);
        ip4 = (EditText) this.findViewById(R.id.ip4);
        clickButton c = new clickButton();
        send = (Button)this.findViewById(R.id.sendIp);
        piger = (Button)this.findViewById(R.id.btnPiger);
        finTour = (Button)this.findViewById(R.id.btnFinTour);
        send.setOnClickListener(c);
        piger.setOnClickListener(c);
        finTour.setOnClickListener(c);
    }


    private class clickButton implements Button.OnClickListener {

        public void onClick(View v) {
            if (v.getId() == send.getId()) {
                try {

                    ArrayList<InetAddress> list = new ArrayList<>();
                    list.add(InetAddress.getByName(ip1.getText().toString()));
                    list.add(InetAddress.getByName(ip2.getText().toString()));
                    list.add(InetAddress.getByName(ip3.getText().toString()));
                    list.add(InetAddress.getByName(ip4.getText().toString()));
                    table = new Table();
                    table.joueurs = list;
                    for (int i = 0; i < table.joueurs.size(); i++) {
                        if (table.joueurs.get(i).equals(InetAddress.getLocalHost().getAddress())) {
                            table.joueurLocal = new Joueur(i, table);
                        }
                    }

                    setContentView(R.layout.activity_game);
                    finTour.setActivated(false);
                    piger.setText("Debuter");

                } catch (UnknownHostException e) {
                }


            }else if(v.getId() == piger.getId()){
                    piger.setText("Piger");
                    finTour.setActivated(false);
                    piger.setActivated(false);
                    table.debuterJeu();
                    table.joueurLocal.multicast = true;
                    table.joueurLocal.trameSent = new Trame(table.joueurLocal.seq, Trame.MESSAGE_BRASSEUR, -42);
                    table.joueurLocal.send();


            }else if(v.getId() == finTour.getId()){

            }else{}
        }
    }
}
