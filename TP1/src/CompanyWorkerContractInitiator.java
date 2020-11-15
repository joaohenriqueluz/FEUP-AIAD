import jade.core.AID;
import jade.core.behaviours.DataStore;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;
import jade.proto.SSIteratedAchieveREResponder;

import java.util.ArrayList;
import java.util.Vector;

public class CompanyWorkerContractInitiator extends ContractNetInitiator {

    private CompanyAgent company;
    private int bestProposal = -1;
    private double bestDistance = -1;
    private ACLMessage request;
    private Position scooterPosition;

    public CompanyWorkerContractInitiator(CompanyAgent a, ACLMessage msg, ACLMessage request,
            Position scooterPosition) {
        super(a, msg);
        this.company = a;
        this.request = request;
        this.scooterPosition = scooterPosition;
    }

    protected Vector prepareCfps(ACLMessage cfp) {
        Vector v = new Vector();
        cfp.setPerformative(ACLMessage.CFP);
        AID[] workerAgents = company.getYellowPagesService().getAgentList("worker");
        if (workerAgents != null) {
            for (AID workerId : workerAgents) {
                cfp.addReceiver(workerId);
            }
        }
        v.add(cfp);
        return v;
    }

    protected void handleAllResponses(Vector responses, Vector acceptances) {
        if (Utility.getVerbose()) {
            System.out.println("** " + this.company.getLocalName() + " got " + responses.size()
                    + " CFP responses from workers! **");
        }
        for (int i = 0; i < responses.size(); i++) {
            ACLMessage msg = ((ACLMessage) responses.get(i)).createReply();
            if (((ACLMessage) responses.get(i)).getPerformative() == ACLMessage.PROPOSE) {
                double proposalDistance = Double.parseDouble(((ACLMessage) responses.get(i)).getContent());
                if (bestProposal == -1) {
                    bestProposal = i;
                    bestDistance = proposalDistance;
                    msg.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                } else if (bestDistance > proposalDistance) {
                    ((ACLMessage) acceptances.get(bestProposal)).setPerformative(ACLMessage.REJECT_PROPOSAL);
                    bestProposal = i;
                    bestDistance = proposalDistance;
                    msg.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                } else {
                    msg.setPerformative(ACLMessage.REJECT_PROPOSAL);
                }
            } else {
                msg.setPerformative(ACLMessage.REJECT_PROPOSAL);
            }
            acceptances.add(msg);
        }
        if (bestProposal > -1) {
            Position nearestStation = this.company.getNearestStation(scooterPosition);
            this.company
                    .updateOperationCosts(bestDistance + Utility.getEuclideanDistance(nearestStation, scooterPosition));
            this.company.printTripsInfo();
            ((ACLMessage) acceptances.get(bestProposal)).setContent("STATION-AT=>" + nearestStation.toString());
        } else {
            ACLMessage response = this.request.createReply();
            response.setPerformative(ACLMessage.FAILURE);
            response.setContent("Could not find available workers");
            if (this.parent != null) {
                DataStore ds = getDataStore();
                ds.put(((SSIteratedAchieveREResponder) parent).REPLY_KEY, response);
            } else {
                this.company.send(response);
            }
        }

    }

    protected void handleAllResultNotifications(Vector resultNotifications) {
        if (resultNotifications.size() == 1) {
            ACLMessage notification = ((ACLMessage) resultNotifications.get(0));
            String content = notification.getContent();
            Utility.log(this.company, notification);
            ArrayList<String> parsed = Utility.parseMessage(content);
            Position nearestScooterPosition = Utility.parsePosition(parsed.get(1));
            ACLMessage response = this.request.createReply();
            response.setPerformative(ACLMessage.INFORM);
            response.setContent("FOUND-WORKER");
            if (this.parent != null) {
                DataStore ds = getDataStore();
                ds.put(((SSIteratedAchieveREResponder) parent).REPLY_KEY, response);
            } else {
                this.company.send(response);
            }
        }
    }

}