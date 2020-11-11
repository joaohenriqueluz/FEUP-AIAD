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
        msg.setContent("PICK-UP=>" + this.scooter.getPosition().toString());
        AID companyAgent = this.scooter.getYellowPagesService().getAgentList("company")[0];
        System.out.println(companyAgent);
        if (companyAgent != null) {
            msg.addReceiver(companyAgent);
        }
    }

    protected void handleAgree(ACLMessage agree) {
        Utility.log(this.scooter, agree);
        this.scooter.setBusy(false);
    }

    protected void handleRefuse(ACLMessage refuse) {
        Utility.log(this.scooter, refuse);
        this.scooter.setBusy(false);
    }

    protected void handleInform(ACLMessage inform) {
        // ...
        Utility.log(this.scooter, inform);
        // Position stationPosition =
        // Utility.parseMessageWithPosition(inform.getContent());
        // ACLMessage reply = inform.createReply();
        // reply.setPerformative(ACLMessage.REQUEST);
        // reply.setContent("GO-TO=>" + decision.toString());
        // this.scooter.send(reply);
    }

    protected void handleFailure(ACLMessage failure) {
        // ...
        Utility.log(this.scooter, failure);

    }
}