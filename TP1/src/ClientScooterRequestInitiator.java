import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

class ClientScooterRequestInitiator extends AchieveREInitiator {
    ClientAgent client;

    public ClientScooterRequestInitiator(ClientAgent client, ACLMessage msg) {
        super(client, msg);
        this.client = client;
    }

    protected void handleAgree(ACLMessage agree) {
        System.out.println("-----------------RECEIVED AGREE ");
        Utility.log(this.client, agree);
        System.out.println(agree.getContent());
    }

    protected void handleRefuse(ACLMessage refuse) {
        System.out.println("-----------------RECEIVED REFUSE ");
        Utility.log(this.client, refuse);
    }

    protected void handleInform(ACLMessage inform) {
        // ...
        System.out.println("-----------------RECEIVED INFORM ");
        Utility.log(this.client, inform);
        Position stationPosition = Utility.parseMessageWithPosition(inform.getContent());
        Position decision = this.client.makeDecision(stationPosition);
        ACLMessage reply = inform.createReply();
        reply.setPerformative(ACLMessage.REQUEST);
        reply.setContent("GO-TO=>" + decision.toString());
        this.client.send(reply);
    }

    protected void handleFailure(ACLMessage failure) {
        System.out.println("-----------------RECEIVED FAILURE ");
        Utility.log(this.client, failure);
        System.out.println("VOU A PÉ!");
    }
}