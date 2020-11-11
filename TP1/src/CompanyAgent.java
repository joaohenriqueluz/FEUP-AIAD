import java.util.ArrayList;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class CompanyAgent extends Agent {

    private ArrayList<Position> chargingStationPositions;
    private String companyName;
    private YellowPagesService yellowPagesService;

    public CompanyAgent(Integer numberOfStations, String name) {
        this.companyName = name;
        chargingStationPositions = new ArrayList<Position>();
        for (int i = 0; i < numberOfStations; i++) {
            chargingStationPositions.add(new Position());
        }
    }

    public ArrayList getChargingStationPositions() {
        return chargingStationPositions;
    }

    public YellowPagesService getYellowPagesService() {
        return this.yellowPagesService;
    }

    public void setYellowPagesService(YellowPagesService yellowPagesService) {
        this.yellowPagesService = yellowPagesService;
    }

    public void setup() {
        yellowPagesService = new YellowPagesService(this, "company", companyName);
        yellowPagesService.register();
        addBehaviour(new CompanyRequestResponder(this, MessageTemplate.MatchPerformative(ACLMessage.REQUEST)));
        System.out.println(getLocalName() + ": starting to work!");
    }

    public Position getNearestStation(Position destination) {
        Position nearest = null;
        for (Position position : chargingStationPositions) {
            if (nearest == null) {
                nearest = position;
            } else if (Utility.getEuclideanDistance(nearest, destination) > Utility.getEuclideanDistance(position,
                    destination)) {
                nearest = position;
            }
        }
        return nearest;
    }

    public void takeDown() {
        System.out.println(getLocalName() + ": done working.");
    }

}
