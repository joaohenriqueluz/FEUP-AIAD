import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;

import java.util.ArrayList;

public class CompanyScooterContractResponder extends ContractNetResponder {

    private ElectricScooterAgent scooter;

    public CompanyScooterContractResponder(ElectricScooterAgent a, MessageTemplate mt) {
        super(a, mt);
        this.scooter = a;
    }

    protected ACLMessage handleCfp(ACLMessage cfp) {
        Utility.log(this.scooter, cfp);
        ACLMessage reply = cfp.createReply();

        if (!this.scooter.isBusy()) {
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
        } else {
            reply.setPerformative(ACLMessage.REFUSE);
        }
        return reply;
    }

    private double calculateDistance(ArrayList<String> tokens) {
        Position position = Utility.parsePosition(tokens.get(1));
        return Utility.getEuclideanDistance(this.scooter.getPosition(), position);
    }

    protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
        System.out.println(this.scooter.getLocalName() + " got a reject...");
    }

    protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) {
        System.out.println(this.scooter.getLocalName() + " got an accept!");
        ACLMessage result = accept.createReply();
        result.setPerformative(ACLMessage.INFORM);
        result.setContent("IM-AT=>" + this.scooter.getPosition().toString() + "--" + this.scooter.getAID().getName());
        this.scooter.addBehaviour(
                new ScooterRequestResponder(this.scooter, MessageTemplate.MatchPerformative(ACLMessage.REQUEST)));
        return result;
    }

}
