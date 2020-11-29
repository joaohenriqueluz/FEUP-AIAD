import sajas.core.behaviours.Behaviour;
import sajas.core.behaviours.DataStore;
import jade.lang.acl.ACLMessage;
import sajas.proto.SSIteratedAchieveREResponder;

public class CalculateNearestStation extends Behaviour {

    private CompanyAgent company;
    private Position position;
    private Boolean done = false;
    private ACLMessage request;

    CalculateNearestStation(CompanyAgent company, ACLMessage request, Position position) {
        super(company);
        this.company = company;
        this.position = position;
        this.request = request;
    }

    public void action() {
        Position nearestStationPosition = this.company.getNearestStation(position);
        ACLMessage response = this.request.createReply();
        response.setContent("NEAREST-STATION=>" + nearestStationPosition.toString());
        response.setPerformative(ACLMessage.INFORM);
        if (this.parent != null) {
            DataStore ds = getDataStore();
            ds.put(((SSIteratedAchieveREResponder) parent).REPLY_KEY, response);
        } else {
            this.company.send(response);
        }
        done = true;
    }

    public boolean done() {
        return done;
    }

}
