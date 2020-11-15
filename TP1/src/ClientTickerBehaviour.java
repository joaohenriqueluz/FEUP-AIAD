import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

public class ClientTickerBehaviour extends TickerBehaviour {

    private ClientAgent client;

    public ClientTickerBehaviour(ClientAgent client, long period) {
        super(client, period);
        this.client = client;
    }

    public void onTick() {
        if (!this.client.isBusy()) {
            this.client.addBehaviour(new RequestScooterInitiator(this.client, new ACLMessage(ACLMessage.REQUEST)));
        }
    }
}
