import sajas.core.AID;
import jade.lang.acl.ACLMessage;
import sajas.proto.AchieveREInitiator;

import java.util.ArrayList;

class RequestScooterInitiator extends AchieveREInitiator {
    ClientAgent client;
    private int n = 0;

    public RequestScooterInitiator(ClientAgent client, ACLMessage msg) {
        super(client, msg);
        this.client = client;
        this.client.setBusy(true);
        // Request: GET-SCOOTER [Position]
        String message = "GET-SCOOTER=>" + this.client.getPosition().toString();
        msg.setContent(message);
        AID companyAgent = client.getYellowPagesService().getAgentList("company")[0];
        if (companyAgent != null) {
            msg.addReceiver(companyAgent);
        }
    }

    protected void handleAgree(ACLMessage agree) {
        Utility.log(this.client, agree);
        ArrayList<String> tokens = Utility.parseMessage(agree.getContent());
        this.client.setScooterPriceRate(Double.parseDouble(tokens.get(1)));
        this.client.setMonetaryIncentive(Double.parseDouble(tokens.get(2)));
    }

    protected void handleRefuse(ACLMessage refuse) {
        Utility.log(this.client, refuse);
        this.client.setBusy(false);
    }

    protected void handleInform(ACLMessage inform) {
        Utility.log(this.client, inform);
        this.client.setDestination(Utility.getDestination(this.client.getPosition()));
        ArrayList<String> parsed = Utility.parseMessage(inform.getContent());
        Position newClientPosition = Utility.parsePosition(parsed.get(1));
        AID scooterAID = new AID(parsed.get(2), true);
        ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
        message.addReceiver(scooterAID);
        message.setContent("DESTINATION=>" + this.client.getDestination().toString());
        this.client.setPosition(newClientPosition);
        this.client.addBehaviour(new ClientScooterRequestInitiator(this.client, message));
    }

    protected void handleFailure(ACLMessage failure) {
        Utility.log(this.client, failure);
        this.client.setBusy(false);
    }
}