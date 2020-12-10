import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sajas.proto.AchieveREResponder;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

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
            case "DROP":
                return parseDrop(message, request);
            case "CHARGE-AT":
                return parseChargeAt(message, request);
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
            response.setPerformative(ACLMessage.CONFIRM);
            response.setContent("Processing Request");
            this.scooter.addBehaviour(new RequestPickUpInitiator(this.scooter, new ACLMessage(ACLMessage.REQUEST)));
            // timer.schedule(task, delay);
        } catch (Exception e) {
            response.setPerformative(ACLMessage.NOT_UNDERSTOOD);
            response.setContent("Corrupted Command");
        }
        return response;
    }

    private ACLMessage parseDrop(ArrayList<String> requestContents, ACLMessage request) {
        ACLMessage response = request.createReply();
        try {
            response.setPerformative(ACLMessage.CONFIRM);
            response.setContent("Dropping scooter...");
            this.scooter.setBusy(false);
        } catch (Exception e) {
            response.setPerformative(ACLMessage.NOT_UNDERSTOOD);
            response.setContent("Corrupted Command");
        }
        return response;
    }

    private ACLMessage parseChargeAt(ArrayList<String> requestContents, ACLMessage request) {
        System.out.println("\tFROM WORKER: " + request.getContent());
        ACLMessage reply = request.createReply();
        reply.setPerformative(ACLMessage.INFORM);
        reply.setContent("Got your message!");
        ArrayList<String> tokens = Utility.parseMessage(request.getContent());
        Position newPosition = Utility.parseMessageWithPosition(request.getContent());
        this.scooter.setPosition(newPosition);
        this.scooter.setTripStartPosition(newPosition);
        this.scooter.setBusy(false);
        return reply;
    }

}