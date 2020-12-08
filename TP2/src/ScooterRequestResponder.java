import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sajas.proto.AchieveREResponder;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

class ScooterRequestResponder extends AchieveREResponder {

    ElectricScooterAgent scooter;
    long delay = 10000L;
    private Timer timer = new Timer("Timer");

    private class RepeatBehaviour extends TimerTask {
        @Override
        public void run() {
            System.out.println(scooter.getLocalName() + " is busy: "+ scooter.isBusy());
            if (scooter.isBusy()) {
                scooter.addBehaviour(new RequestPickUpInitiator(scooter, new ACLMessage(ACLMessage.REQUEST)));
            } else{
                timer.cancel();
            }
        }
    }

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
            timer.scheduleAtFixedRate(new RepeatBehaviour(), 0, 10000);
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

    private TimerTask createTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                // System.out.println("Task performed on: " + new Date() + "n" +
                // "Thread's name: " + Thread.currentThread().getName());
                if (scooter.isBusy()) {
                    scooter.addBehaviour(new RequestPickUpInitiator(scooter, new ACLMessage(ACLMessage.REQUEST)));
                    timer.scheduleAtFixedRate(createTimerTask(), 0, 10000);
                }
            }
        };
    }

}