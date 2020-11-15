import jade.proto.ContractNetResponder;
import java.util.ArrayList;
import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class WorkerContractResponder extends ContractNetResponder {

    private WorkerAgent worker;
    private Position positionOfScooter;
    private AID scooterAID;

    public WorkerContractResponder(WorkerAgent worker, MessageTemplate mt) {
        super(worker, mt);
        System.out.println("Constructor WorkerContractResponder");
        this.worker = worker;
    }

    protected ACLMessage handleCfp(ACLMessage cfp) {
        Utility.log(this.worker, cfp);
        ACLMessage reply = cfp.createReply();
        ArrayList<String> tokens = Utility.parseMessage(cfp.getContent());
        double distance;
        System.out.println("\n\n\n" + worker.getLocalName()+ "BUSY: "+ this.worker.isBusy()+ "\n\n\n");
        if (!this.worker.isBusy()) {
            System.out.println(this.worker.getLocalName() + " is NOT Busy");
            switch (tokens.get(0)) {
                case "GET-WORKER":
                    scooterAID = new AID(tokens.get(2));
                    positionOfScooter = Utility.parsePosition(tokens.get(1));
                    distance = Utility.getEuclideanDistance(this.worker.getPosition(), positionOfScooter);
                    reply.setContent("" + distance);
                    reply.setPerformative(ACLMessage.PROPOSE);
                    break;
                default:
                    break;
            }
        }else{
            System.out.println(this.worker.getLocalName() + " is Busy");
            reply.setPerformative(ACLMessage.REFUSE);
        }
        return reply;
    }

    protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
        System.out.println(this.worker.getLocalName() + " got a reject...");
    }

    protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) {
        Position stationPosition = Utility.parseMessageWithPosition(accept.getContent());
        System.out.println(this.worker.getLocalName() + " got an accept!");
        this.worker.setPosition(positionOfScooter);
        this.worker.setBusy(true);
        ACLMessage result = accept.createReply();
        result.setPerformative(ACLMessage.INFORM);
        result.setContent("IM-AT=>" + this.worker.getPosition().toString() + "--" + this.worker.getAID().getName());
        ACLMessage message = new ACLMessage();
        message.setPerformative(ACLMessage.INFORM);
        message.setContent("CHARGE-AT=>" + stationPosition.toString());
        message.addReceiver(scooterAID);
        this.worker.send(message);
        this.worker.setBusy(false);
        // this.worker.addBehaviour(new ScooterRequestResponder(this.worker,
        // MessageTemplate.MatchPerformative(ACLMessage.REQUEST)));
        return result;
    }

}
