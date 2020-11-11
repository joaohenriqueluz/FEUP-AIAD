import java.util.ArrayList;
import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREResponder;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;
import jade.core.behaviours.DataStore;

class ScooterRequestResponder extends AchieveREResponder {

    ElectricScooterAgent scooter;

    public ScooterRequestResponder(ElectricScooterAgent scooter, MessageTemplate mt) {
        super(scooter, mt);
        this.scooter = scooter;
    }

    protected ACLMessage handleRequest(ACLMessage request) {
        ACLMessage reply = parseRequest(request);
        return reply;
    }

    public ACLMessage parseRequest(ACLMessage request) {
        Utility.log(this.scooter, request);
        ArrayList<String> message = Utility.parseMessage(request.getContent());
        switch (message.get(0)) {
            case "DESTINATION":
                this.scooter.setBusy(true);
                return getDestinationInfo(message, request);
            case "GO-TO":
                return parseGoTo(message, request);

            default:
                break;
        }

        return request.createReply();
    }

    private ACLMessage getDestinationInfo(ArrayList<String> requestContents, ACLMessage request) {
        Position position = Utility.parsePosition(requestContents.get(1));
        ACLMessage response = request.createReply();
        ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
        message.setContent("GET-STATION=>" + position.toString());
        try {
            response.setPerformative(ACLMessage.AGREE);
            response.setContent("Processing Request");
            registerPrepareResultNotification(new RequestStationInitiator(this.scooter, message, request));
        } catch (Exception e) {
            response.setPerformative(ACLMessage.NOT_UNDERSTOOD);
            response.setContent("Corrupted Command");
        }

        return response;
    }

    private ACLMessage parseGoTo(ArrayList<String> requestContents, ACLMessage request) {
        Position newScooterPosition = Utility.parseMessageWithPosition(request.getContent());
        ACLMessage response = request.createReply();
        this.scooter.setPosition(newScooterPosition);
        try {
            response.setPerformative(ACLMessage.AGREE);
            response.setContent("Processing Request");
            this.scooter.addBehaviour(new RequestPickUpResponder(this.scooter, new ACLMessage(ACLMessage.REQUEST)));
        } catch (Exception e) {
            response.setPerformative(ACLMessage.NOT_UNDERSTOOD);
            response.setContent("Corrupted Command");
        }
        return response;
    }

}