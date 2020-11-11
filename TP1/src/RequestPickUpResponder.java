import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

class RequestPickUpResponder extends AchieveREInitiator {
    ElectricScooterAgent scooter;

    public RequestPickUpResponder(ElectricScooterAgent scooter, ACLMessage msg) {
        super(scooter, msg);
        this.scooter = scooter;
        msg.setContent("PICK-UP=>" + this.scooter.getPosition().toString() + "--" + this.scooter.getAID().getName());
        AID companyAgent = this.scooter.getYellowPagesService().getAgentList("company")[0];
        System.out.println(companyAgent);
        if (companyAgent != null) {
            msg.addReceiver(companyAgent);
        }
    }

    protected void handleAgree(ACLMessage agree) {
        Utility.log(this.scooter, agree);
    }

    protected void handleRefuse(ACLMessage refuse) {
        Utility.log(this.scooter, refuse);
        this.scooter.setBusy(false);
    }

    protected void handleInform(ACLMessage inform) {
        // ...
        Utility.log(this.scooter, inform);
        ACLMessage msg = this.scooter.receive();
        if (msg != null) {
            ACLMessage reply = msg.createReply();
            reply.setPerformative(ACLMessage.INFORM);
            reply.setContent("Got your message!");
            this.scooter.send(reply);
        } else {
            block();

        }
        Position newPosition = Utility.parseMessageWithPosition(msg.getContent());
        this.scooter.setPosition(newPosition);
        this.scooter.setBusy(false);
    }

    protected void handleFailure(ACLMessage failure) {
        Utility.log(this.scooter, failure);
    }
}