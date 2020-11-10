import jade.core.Agent;
import java.util.ArrayList;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import jade.proto.AchieveREResponder;
import jade.core.behaviours.DataStore;


class RequestStationInitiator extends AchieveREInitiator {
    private int n = 0;
    ElectricScooterAgent scooter;
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
        // ...
        System.out.println(agree.getContent());
    }

    protected void handleRefuse(ACLMessage refuse) {
        // ...
    }

    protected void handleInform(ACLMessage inform) {
        Utility.log(this.scooter,inform);

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
        // ...
    }
}