import jade.core.AID;
import jade.core.behaviours.DataStore;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;
import jade.proto.SSIteratedAchieveREResponder;

import java.util.ArrayList;
import java.util.Vector;

public class CompanyScooterContractInitator extends ContractNetInitiator {

    private CompanyAgent company;
    private int bestProposal = -1;
    private double bestDistance = -1;
    private ACLMessage request;

    public CompanyScooterContractInitator(CompanyAgent a, ACLMessage msg, ACLMessage request) {
        super(a, msg);
        this.company = a;
        this.request = request;
    }

    protected Vector prepareCfps(ACLMessage cfp) {
        Vector v = new Vector();
        cfp.setPerformative(ACLMessage.CFP);
        AID[] scooterAgents = company.getYellowPagesService().getAgentList("electic-scooter");
        if (scooterAgents != null) {
            for (AID scooterId : scooterAgents) {
                cfp.addReceiver(scooterId);
            }
        }
        v.add(cfp);
        return v;
    }

    protected void handleAllResponses(Vector responses, Vector acceptances) {

        if (Utility.getVerbose()) {
            System.out.println("** " + this.company.getLocalName() + " got " + responses.size()
                    + " CFP responses form scooters! **");
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
        if (bestProposal == -1) {
            ACLMessage response = this.request.createReply();
            response.setPerformative(ACLMessage.FAILURE);
            response.setContent("Could not find available scooters");
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
            response.setContent("SCOOTER-AT=>" + nearestScooterPosition.toString() + "--" + parsed.get(2));
            if (this.parent != null) {
                DataStore ds = getDataStore();
                ds.put(((SSIteratedAchieveREResponder) parent).REPLY_KEY, response);
            } else {
                this.company.send(response);
            }
        }

    }
}