import jade.lang.acl.ACLMessage;
import sajas.proto.AchieveREInitiator;

class ClientScooterRequestInitiator extends AchieveREInitiator {
    ClientAgent client;

    public ClientScooterRequestInitiator(ClientAgent client, ACLMessage msg) {
        super(client, msg);
        this.client = client;
    }

    protected void handleAgree(ACLMessage agree) {
        Utility.log(this.client, agree);
    }

    protected void handleRefuse(ACLMessage refuse) {
        Utility.log(this.client, refuse);
        this.client.setBusy(false);
    }

    protected void handleInform(ACLMessage inform) {
        // ...
        Utility.log(this.client, inform);
        ACLMessage reply = inform.createReply();

        Position stationPosition = Utility.parseMessageWithPosition(inform.getContent());
        if (Utility.getEuclideanDistance(stationPosition, this.client.getPosition()) == 0) {
            reply.setPerformative(ACLMessage.REQUEST);
            reply.setContent("DROP=>");
        } else {
            Position decision = this.client.makeDecision(stationPosition);
            reply.setPerformative(ACLMessage.REQUEST);
            reply.setContent("GO-TO=>" + decision.toString());

        }
        this.client.setBusy(false);
        this.client.send(reply);
    }

    protected void handleFailure(ACLMessage failure) {
        Utility.log(this.client, failure);
        this.client.setBusy(false);
    }
}