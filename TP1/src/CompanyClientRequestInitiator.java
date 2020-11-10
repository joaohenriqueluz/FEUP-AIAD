import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

class CompanyClientRequestInitiator extends AchieveREInitiator {
    private int n = 0;
    ClientAgent client;

    public CompanyClientRequestInitiator(ClientAgent client, ACLMessage msg) {
        super(client, msg);
        this.client = client;
        // Request: GET-SCOOTER [Position]
        String message = "GET-SCOOTER:" + this.client.getPosition().toString();
        msg.setContent(message);
        AID companyAgent = client.getYellowPagesService().getAgentList("company")[0];
        System.out.println(companyAgent);
        System.out.println("Client Sent = " + msg.getContent());
        
        if (companyAgent != null) {
            msg.addReceiver(companyAgent);
        }
    }

    protected void handleAgree(ACLMessage agree) {
        // ...
        System.out.println(agree.getContent());
    }

    protected void handleRefuse(ACLMessage refuse) {
        // ...
    }

    protected void handleInform(ACLMessage inform) {
        // ...
        Utility.log(this.client,inform);
        Position newClientPosition = Utility.parseMessageWithPosition(inform.getContent());
        this.client.setPosition(newClientPosition);
        // this.client.addBehaviour(new CompanyClientRequestInitiator(this.client, new ACLMessage(ACLMessage.REQUEST)));
        this.client.setDestination(Utility.getDestination());
    }

    protected void handleFailure(ACLMessage failure) {
        // ...
    }
}