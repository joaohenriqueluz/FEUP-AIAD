import java.util.ArrayList;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;

public class CompanyScooterContractResponder extends ContractNetResponder {

    private ElectricScooterAgent scooter;

    public CompanyScooterContractResponder(ElectricScooterAgent a, MessageTemplate mt) {
        super(a, mt);
        System.out.println("Constructor CompanyScooterContractResponder");
        this.scooter = a;
    }

    protected ACLMessage handleCfp(ACLMessage cfp) {
        Utility.log(this.scooter, cfp);
        ACLMessage reply = cfp.createReply();
        ArrayList<String> tokens = Utility.parseMessage(cfp.getContent());
        double distance;
        switch (tokens.get(0)) {
            case "NEAREST-SCOOTER":
                distance = calculateDistance(tokens);
                reply.setContent("" + distance);
                reply.setPerformative(ACLMessage.PROPOSE);
                break;
            default:
                break;
        }
        return reply;
    }

    private double calculateDistance(ArrayList<String> tokens) {
        Position position = Utility.parsePosition(tokens.get(1));
        return Utility.getEuclideanDistance(this.scooter.getPosition(), position);
    }


    protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
        System.out.println(myAgent.getLocalName() + " got a reject...");
    }

    protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) {
        System.out.println(myAgent.getLocalName() + " got an accept!");
        ACLMessage result = accept.createReply();
        result.setPerformative(ACLMessage.INFORM);
        result.setContent("POSITION:"+ this.scooter.getPosition().toString());
        return result;
    }

}
