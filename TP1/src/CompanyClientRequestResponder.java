import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREResponder;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;
import jade.core.behaviours.DataStore;

class CompanyClientRequestResponder extends AchieveREResponder {
    CompanyAgent company;

    public CompanyClientRequestResponder(CompanyAgent company, MessageTemplate mt) {
        super(company, mt);
        this.company = company;
    }

    protected ACLMessage handleRequest(ACLMessage request) {
        ACLMessage reply = parseRequest(request);
        return reply;
    }

    public ACLMessage parseRequest(ACLMessage request) {
        System.out.println(this.company.getLocalName() + " Received = " + request.getContent());
        String[] tokens = request.getContent().split(":");
        switch (tokens[0]) {
            case "GET-SCOOTER":
                return parseGetScooter(tokens, request);
            default:
                System.out.println("DEFAULTTTT");
                break;
        }

        return request.createReply();
    }

    private ACLMessage parseGetScooter(String[] tokens, ACLMessage request) {
        String[] coordenates = tokens[1].split(",");
        Position position = new Position(Integer.parseInt(coordenates[0]), Integer.parseInt(coordenates[1]));
        ACLMessage response = request.createReply();
        ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
        message.setContent("GET-SCOOTER:" + coordenates[0] + "," + coordenates[1]);
        try {
            response.setPerformative(ACLMessage.AGREE);
            response.setContent("Processing Request");
            registerPrepareResultNotification(new CompanyScooterContractInitator(this.company, message, request));
        } catch (Exception e) {
            response.setPerformative(ACLMessage.NOT_UNDERSTOOD);
            response.setContent("Corrupted Command");
        }

        return response;

        // AID[] scooterAgents =
        // company.getYellowPagesService().getAgentList("electic-scooter");
        // System.out.println(scooterAgents);
        // if (scooterAgents != null) {
        // for (AID scooterId : scooterAgents) {
        // message.addReceiver(scooterId);
        // }
        // }
        // company.send(message);

    }

}