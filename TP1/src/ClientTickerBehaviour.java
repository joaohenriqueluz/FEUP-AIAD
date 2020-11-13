import jade.core.behaviours.TickerBehaviour;
import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class ClientTickerBehaviour extends TickerBehaviour {

    private ClientAgent client;

    public ClientTickerBehaviour(ClientAgent client, long period) {
        super(client, period);
        this.client = client;
    }

    public void onTick(){
        this.client.addBehaviour(new RequestScooterInitiator(this.client, new ACLMessage(ACLMessage.REQUEST)));
    }
}
