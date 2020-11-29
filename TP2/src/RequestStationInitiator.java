import sajas.core.AID;
import sajas.core.behaviours.DataStore;
import jade.lang.acl.ACLMessage;
import sajas.proto.AchieveREInitiator;
import sajas.proto.AchieveREResponder;

import java.util.ArrayList;

class RequestStationInitiator extends AchieveREInitiator {
    ElectricScooterAgent scooter;
    private int n = 0;
    private ACLMessage request;

    public RequestStationInitiator(ElectricScooterAgent scooter, ACLMessage msg, ACLMessage request) {
        super(scooter, msg);
        this.scooter = scooter;
        this.request = request;
        AID companyAgent = this.scooter.getYellowPagesService().getAgentList("company")[0];
        if (companyAgent != null) {
            msg.addReceiver(companyAgent);
        }
    }

    protected void handleAgree(ACLMessage agree) {
        Utility.log(this.scooter, agree);
    }

    protected void handleRefuse(ACLMessage refuse) {
        Utility.log(this.scooter, refuse);
    }

    protected void handleInform(ACLMessage inform) {
        Utility.log(this.scooter, inform);

        ArrayList<String> parsed = Utility.parseMessage(inform.getContent());
        Position nearestStationPosition = Utility.parsePosition(parsed.get(1));

        ACLMessage response = this.request.createReply();
        response.setPerformative(ACLMessage.INFORM);
        response.setContent("NEAREST-STATION=>" + nearestStationPosition.toString());

        if (this.parent != null) {
            DataStore ds = getDataStore();
            ds.put(((AchieveREResponder) parent).RESULT_NOTIFICATION_KEY, response);
        } else {
            this.scooter.send(response);
        }
    }

    protected void handleFailure(ACLMessage failure) {
        Utility.log(this.scooter, failure);
    }
}