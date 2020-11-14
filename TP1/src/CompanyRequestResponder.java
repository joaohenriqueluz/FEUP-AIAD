import java.util.ArrayList;
import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREResponder;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;
import jade.core.behaviours.DataStore;

class CompanyRequestResponder extends AchieveREResponder {
    CompanyAgent company;

    public CompanyRequestResponder(CompanyAgent company, MessageTemplate mt) {
        super(company, mt);
        this.company = company;
    }

    protected ACLMessage handleRequest(ACLMessage request) {
        ACLMessage reply = parseRequest(request);
        return reply;
    }

    public ACLMessage parseRequest(ACLMessage request) {
        Utility.log(this.company, request);
        ArrayList<String> message = Utility.parseMessage(request.getContent());
        switch (message.get(0)) {
            case "GET-SCOOTER":
                return parseGetScooter(message, request);
            case "GET-STATION":
                return parseGetStation(message, request);
            case "PICK-UP":
                return parsePickUp(message, request);
            default:
                break;
        }

        return request.createReply();
    }

    private ACLMessage parseGetScooter(ArrayList<String> requestContents, ACLMessage request) {
        Position position = Utility.parsePosition(requestContents.get(1));
        ACLMessage response = request.createReply();
        ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
        message.setContent("NEAREST-SCOOTER=>" + position.toString());
        try {
            response.setPerformative(ACLMessage.AGREE);
            response.setContent("Finding nearest available scooter=>" + this.company.getScooterPriceRate() + "--"
                    + this.company.getMonetaryIncentive());
            registerPrepareResultNotification(new CompanyScooterContractInitator(this.company, message, request));
        } catch (Exception e) {
            response.setPerformative(ACLMessage.NOT_UNDERSTOOD);
            response.setContent("Corrupted Command");
        }

        return response;
    }

    private ACLMessage parseGetStation(ArrayList<String> requestContents, ACLMessage request) {
        Position position = Utility.parsePosition(requestContents.get(1));

        ACLMessage response = request.createReply();
        try {
            response.setPerformative(ACLMessage.AGREE);
            response.setContent("Finding nearest station for position " + position.toString());
            registerPrepareResultNotification(new CalculateNearestStation(this.company, request, position));
        } catch (Exception e) {
            response.setPerformative(ACLMessage.NOT_UNDERSTOOD);
            response.setContent("Corrupted Command");
        }
        return response;
    }

    private ACLMessage parsePickUp(ArrayList<String> requestContents, ACLMessage request) {
        Position scooterPosition = Utility.parsePosition(requestContents.get(1));
        double scooterDistance = Double.parseDouble(requestContents.get(2));
        String scooterAID = requestContents.get(3);
        double tripPrice = 0;
        ACLMessage message = new ACLMessage(ACLMessage.CFP);
        message.setContent("GET-WORKER=>" + scooterPosition.toString() + "--" + scooterAID);
        ACLMessage response = request.createReply();
        try {
            if (!this.company.getNearestStation(scooterPosition).equals(scooterPosition)) {
                this.company.updateNetIncome(scooterDistance);
                response.setPerformative(ACLMessage.AGREE);
                response.setContent("Finding available workers");
                registerPrepareResultNotification(
                        new CompanyWorkerContractInitiator(this.company, message, request, scooterPosition));
            } else {
                this.company.updateNetIncomeWithoutIncentive(scooterDistance);
                response.setPerformative(ACLMessage.REFUSE);
                response.setContent("Already in station, no need");
                this.company.printTripsInfo();
            }
        } catch (Exception e) {
            response.setPerformative(ACLMessage.NOT_UNDERSTOOD);
            response.setContent("Corrupted Command");
        }
        return response;
    }

}