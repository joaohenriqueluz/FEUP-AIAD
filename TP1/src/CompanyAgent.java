import java.util.ArrayList;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class CompanyAgent extends Agent {

    private ArrayList<Position> chargingStationPositions;
    private String companyName;
    private YellowPagesService yellowPagesService;
    private int numberOfTrips;
    private int numberOfSuccessfulTrips;

    public CompanyAgent(int numberOfStations, String name) {
        this.companyName = name;
        this.numberOfSuccessfulTrips = 0;
        this.numberOfTrips = 0;
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

    public int getNumberOfSuccessfulTrips() {
        return this.numberOfSuccessfulTrips;
    }

    public void updateNumberOfSuccessfulTrips(int numberOfSuccessfulTrips) {
        this.numberOfSuccessfulTrips += numberOfSuccessfulTrips;
    }

    public int getNumberOfTrips() {
        return this.numberOfTrips;
    }

    public void updateNumberOfTrips(int numberOfTrips) {
        this.numberOfTrips += numberOfTrips;
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

    public void printTripsInfo() {
        System.out.println("Number of successful trips: " + numberOfSuccessfulTrips);
        System.out.println("Number of total trips: " + numberOfTrips);
        Double percentageSuccessful = 0.0;
        String roundedPercentage = "";
        if (numberOfTrips != 0) {
            percentageSuccessful = numberOfSuccessfulTrips / (double) numberOfTrips * 100;
            roundedPercentage = String.format("%.2f", percentageSuccessful);
        }
        System.out.println("Percentage of successful trips: " + roundedPercentage + "%");
    }

    public void takeDown() {
        System.out.println(getLocalName() + ": done working.");
    }

}
