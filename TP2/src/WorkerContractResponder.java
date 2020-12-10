import sajas.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sajas.proto.ContractNetResponder;

import java.util.ArrayList;

public class WorkerContractResponder extends ContractNetResponder {

    private WorkerAgent worker;
    private Position positionOfScooter;
    private AID scooterAID;

    public WorkerContractResponder(WorkerAgent worker, MessageTemplate mt) {
        super(worker, mt);
        this.worker = worker;
    }

    protected ACLMessage handleCfp(ACLMessage cfp) {
        Utility.log(this.worker, cfp);
        ACLMessage reply = cfp.createReply();
        ArrayList<String> tokens = Utility.parseMessage(cfp.getContent());
        double distance;
        if (!this.worker.isBusy()) {
            switch (tokens.get(0)) {
                case "GET-WORKER":
                    scooterAID = new AID(tokens.get(2),true);
                    positionOfScooter = Utility.parsePosition(tokens.get(1));
                    distance = Utility.getEuclideanDistance(this.worker.getPosition(), positionOfScooter);
                    reply.setContent("" + distance);
                    reply.setPerformative(ACLMessage.PROPOSE);
                    break;
                default:
                    break;
            }
        } else {
            reply.setPerformative(ACLMessage.REFUSE);
        }
        return reply;
    }

    protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
        if (Utility.getVerbose()) {
            System.out.println(this.worker.getLocalName() + " got a reject...");
        }
    }

    protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) {
        this.worker.setBusy(true);
        System.out.println("\n\tPus BUSY ");
        Position stationPosition = Utility.parseMessageWithPosition(accept.getContent());
        if (Utility.getVerbose()) {
            System.out.println(this.worker.getLocalName() + " got an accept!");
        }
        ACLMessage result = accept.createReply();
        result.setPerformative(ACLMessage.INFORM);
        result.setContent("IM-AT=>" + this.worker.getPosition().toString() + "--" + this.worker.getAID().getName());
        System.out.println("\tsengding to: "+ scooterAID);
        ACLMessage message = new ACLMessage();
        message.setPerformative(ACLMessage.REQUEST);
        message.setContent("CHARGE-AT=>" + stationPosition.toString());
        message.addReceiver(scooterAID);
        this.worker.send(message);
        this.worker.setPosition(stationPosition);
        this.worker.setBusy(false);
        return result;
    }

}
