import jade.lang.acl.ACLMessage;
import jade.proto.SSIteratedAchieveREResponder;

import java.util.ArrayList;

class CompanyRequestResponder extends SSIteratedAchieveREResponder {
    CompanyAgent company;

    public CompanyRequestResponder(CompanyAgent company, ACLMessage message) {
        super(company, message);
        this.company = company;
        parseRequest(message);
    }

    public void parseRequest(ACLMessage request) {
        Utility.log(this.company, request);
        ArrayList<String> message = Utility.parseMessage(request.getContent());
        switch (message.get(0)) {
            case "GET-SCOOTER":
                parseGetScooter(message, request);
                break;
            case "GET-STATION":
                parseGetStation(message, request);
                break;
            case "PICK-UP":
                parsePickUp(message, request);
                break;
            default:
                break;
        }
        // return request.createReply();
    }

    private void parseGetScooter(ArrayList<String> requestContents, ACLMessage request) {
        Position position = Utility.parsePosition(requestContents.get(1));
        ACLMessage response = request.createReply();
        ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
        message.setContent("NEAREST-SCOOTER=>" + position.toString());
        try {
            response.setPerformative(ACLMessage.AGREE);
            response.setContent("Finding nearest available scooter=>" + this.company.getScooterPriceRate() + "--"
                    + this.company.getMonetaryIncentive());
            registerHandleRequest(new CompanyScooterContractInitator(this.company, message, request));
        } catch (Exception e) {
            response.setPerformative(ACLMessage.NOT_UNDERSTOOD);
            response.setContent("Corrupted Command");
        }
        this.company.send(response);
    }

    private void parseGetStation(ArrayList<String> requestContents, ACLMessage request) {
        Position position = Utility.parsePosition(requestContents.get(1));

        ACLMessage response = request.createReply();
        try {
            response.setPerformative(ACLMessage.AGREE);
            response.setContent("Finding nearest station for position " + position.toString());
            registerHandleRequest(new CalculateNearestStation(this.company, request, position));
            this.company.send(response);
        } catch (Exception e) {
            response.setPerformative(ACLMessage.NOT_UNDERSTOOD);
            response.setContent("Corrupted Command");
        }
        this.company.send(response);
    }

    private void parsePickUp(ArrayList<String> requestContents, ACLMessage request) {
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
                registerHandleRequest(
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

        this.company.send(response);
    }

}