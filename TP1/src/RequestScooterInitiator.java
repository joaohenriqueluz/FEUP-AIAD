import jade.core.Agent;

import java.util.ArrayList;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

class RequestScooterInitiator extends AchieveREInitiator {
    private int n = 0;
    ClientAgent client;

    public RequestScooterInitiator(ClientAgent client, ACLMessage msg) {
        super(client, msg);
        this.client = client;
        // Request: GET-SCOOTER [Position]
        String message = "GET-SCOOTER=>" + this.client.getPosition().toString();
        msg.setContent(message);
        AID companyAgent = client.getYellowPagesService().getAgentList("company")[0];
        System.out.println(companyAgent);
        System.out.println(this.client.getClientName() + " Sent = " + msg.getContent());

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
    }

    protected void handleInform(ACLMessage inform) {
        Utility.log(this.client, inform);
        this.client.setDestination(Utility.getDestination(this.client.getPosition()));
        ArrayList<String> parsed = Utility.parseMessage(inform.getContent());
        Position newClientPosition = Utility.parsePosition(parsed.get(1));
        AID scooterAID = new AID(parsed.get(2));
        ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
        message.addReceiver(scooterAID);
        message.setContent("DESTINATION=>" + this.client.getDestination().toString());
        this.client.setPosition(newClientPosition);
        this.client.addBehaviour(new ClientScooterRequestInitiator(this.client, message));
    }

    protected void handleFailure(ACLMessage failure) {
        Utility.log(this.client, failure);
    }
}