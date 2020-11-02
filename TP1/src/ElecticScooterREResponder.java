import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREResponder;
import jade.lang.acl.MessageTemplate;

class ElecticScooterREResponder extends AchieveREResponder {
    ElectricScooterAgent scooter;

    public ElecticScooterREResponder(ElectricScooterAgent scooter, MessageTemplate mt) {
        super(scooter, mt);
        this.scooter = scooter;
    }

    protected ACLMessage handleRequest(ACLMessage request) {
        return parseRequest(request);
    }

    public ACLMessage parseRequest(ACLMessage request) {
        System.out.println(this.scooter.getLocalName() + ": " + request);
        String[] tokens = request.getContent().split(":");
        double distance;
        ACLMessage reply = request.createReply();
        switch (tokens[0]) {
            case "GET-SCOOTER":
                System.out.println(tokens);
                distance = parseGetScooter(tokens);
                reply.setContent("SCOOTER-DISTANCE:" + distance);
                reply.setPerformative(ACLMessage.AGREE);
                break;
            default:
                break;
        }
        System.out.println(reply);
        return reply;
    }

    private double parseGetScooter(String[] tokens) {
        String[] coordenates = tokens[1].split(",");
        Position position = new Position(Integer.parseInt(coordenates[0]), Integer.parseInt(coordenates[1]));
        return Utility.getEuclideanDistance(this.scooter.getPosition(), position);
    }
    // protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage
    // response) {
    // ACLMessage result = request.createReply();
    // // ...
    // return result;
    // }
}