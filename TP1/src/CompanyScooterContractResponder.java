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
        System.out.println(this.scooter.getLocalName() + " Received: " + cfp);
        ACLMessage reply = cfp.createReply();
        String[] tokens = cfp.getContent().split(":");
        double distance;
        switch (tokens[0]) {
            case "GET-SCOOTER":
                distance = parseGetScooter(tokens);
                reply.setContent("" + distance);
                reply.setPerformative(ACLMessage.PROPOSE);
                break;
            default:
                break;
        }
        System.out.println(this.scooter.getLocalName() + "answered: " + reply.getContent());
        return reply;
    }

    private double parseGetScooter(String[] tokens) {
        String[] coordenates = tokens[1].split(",");
        Position position = new Position(Integer.parseInt(coordenates[0]), Integer.parseInt(coordenates[1]));
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
