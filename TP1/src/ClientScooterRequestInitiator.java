import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import jdk.jshell.execution.Util;

class ClientScooterRequestInitiator extends AchieveREInitiator {
    ClientAgent client;

    public ClientScooterRequestInitiator(ClientAgent client, ACLMessage msg) {
        super(client, msg);
        this.client = client;
    }

    protected void handleAgree(ACLMessage agree) {
        System.out.println(agree.getContent());
    }

    protected void handleRefuse(ACLMessage refuse) {
        // ...
    }

    protected void handleInform(ACLMessage inform) {
        // ...
        Utility.log(this.client,inform);
        // Position newClientPosition = Utility.parseMessageWithPosition(inform.getContent());
        // this.client.setPosition(newClientPosition);
		// this.client.addBehaviour(new ClientScooterRequestInitiator(this, new ACLMessage(ACLMessage.REQUEST)));
    }

    protected void handleFailure(ACLMessage failure) {
        // ...
    }
}