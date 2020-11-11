import jade.proto.ContractNetResponder;
import java.util.ArrayList;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class WorkerContractResponder extends ContractNetResponder {

    private WorkerAgent worker;
    private Position positionOfScooter;

    public WorkerContractResponder(WorkerAgent a, MessageTemplate mt) {
        super(a, mt);
        System.out.println("Constructor WorkerContractResponder");
        this.worker = a;
    }

    protected ACLMessage handleCfp(ACLMessage cfp) {
        Utility.log(this.worker, cfp);
        ACLMessage reply = cfp.createReply();
        ArrayList<String> tokens = Utility.parseMessage(cfp.getContent());
        double distance;
        switch (tokens.get(0)) {
            case "GET-WORKER":
                positionOfScooter = Utility.parsePosition(tokens.get(1));
                distance = Utility.getEuclideanDistance(this.worker.getPosition(),positionOfScooter);
                reply.setContent("" + distance);
                reply.setPerformative(ACLMessage.PROPOSE);
                break;
            default:
                break;
        }
        return reply;
    }

    protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
        System.out.println(this.worker.getLocalName() + " got a reject...");
    }

    protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) {
        System.out.println(this.worker.getLocalName() + " got an accept!");
        this.worker.setPosition(positionOfScooter);
        ACLMessage result = accept.createReply();
        result.setPerformative(ACLMessage.INFORM);
        result.setContent("IM-AT=>" + this.worker.getPosition().toString() + "--" + this.worker.getAID().getName());
        // this.worker.addBehaviour(new ScooterRequestResponder(this.worker,
        // MessageTemplate.MatchPerformative(ACLMessage.REQUEST)));
        return result;
    }

}
