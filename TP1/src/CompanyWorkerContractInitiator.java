import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;

import java.util.ArrayList;
import java.util.Vector;
import jade.core.behaviours.DataStore;
import jade.proto.AchieveREResponder;

public class CompanyWorkerContractInitiator extends ContractNetInitiator {

    private CompanyAgent company;
    private int bestProposal = -1;
    private double bestDistance = -1;
    private ACLMessage request;

    public CompanyWorkerContractInitiator(CompanyAgent a, ACLMessage msg, ACLMessage request) {
        super(a, msg);
        this.company = a;
        this.request = request;
    }

    protected Vector prepareCfps(ACLMessage cfp) {
        Vector v = new Vector();
        cfp.setPerformative(ACLMessage.CFP);
        AID[] workerAgents = company.getYellowPagesService().getAgentList("worker");
        System.out.println(workerAgents.length);
        if (workerAgents != null) {
            for (AID workerId : workerAgents) {
                cfp.addReceiver(workerId);
            }
        }
        v.add(cfp);
        return v;
    }

    protected void handleAllResponses(Vector responses, Vector acceptances) {

        System.out.println("got " + responses.size() + " responses!");
        for (int i = 0; i < responses.size(); i++) {
            ACLMessage msg = ((ACLMessage) responses.get(i)).createReply();
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
            acceptances.add(msg);
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
                ds.put(((AchieveREResponder) parent).RESULT_NOTIFICATION_KEY, response);
            } else {
                this.company.send(response);
            }
        }

    }

}