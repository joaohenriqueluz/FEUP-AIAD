import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

public class ClientAgent extends Agent {

    private String clientName;
    private Position position;
    private Position destination;
    private double weatherWeight;
    private double distanceWeight;
    private YellowPagesService yellowPagesService;
    private double scooterPriceRate;
    private double monetaryIncentive;

    public ClientAgent(String name) {
        clientName = name;
        position = new Position();
    }

    public String getClientName() {
        return this.clientName;
    }

    public void setClientName(String newName) {
        this.clientName = newName;
    }

    public Position getPosition() {
        return this.position;
    }

    public Position getDestination() {
        return this.destination;
    }

    public void setPosition(Position newPosition) {
        System.out.println(getLocalName() + " new position is " + newPosition.toString());
        this.position = newPosition;
    }

    public void setDestination(Position newDestination) {
        System.out.println(getLocalName() + " new destination is " + newDestination.toString());
        this.destination = newDestination;
    }

    public void setScooterPriceRate(double scooterPriceRate) {
        this.scooterPriceRate = scooterPriceRate;
    }

    public void setMonetaryIncentive(double monetaryIncentive) {
        this.monetaryIncentive = monetaryIncentive;
    }

    public double getScooterPriceRate() {
        return this.scooterPriceRate;
    }

    public double getMonetaryIncentive() {
        return this.monetaryIncentive;
    }

    public YellowPagesService getYellowPagesService() {
        return this.yellowPagesService;
    }

    public void setYellowPagesService(YellowPagesService yellowPagesService) {
        this.yellowPagesService = yellowPagesService;
    }

    public void setup() {
        yellowPagesService = new YellowPagesService(this, "client", clientName);
        yellowPagesService.register();
        addBehaviour(new RequestScooterInitiator(this, new ACLMessage(ACLMessage.REQUEST)));
        // addBehaviour(new RequestOther(this));
        System.out.println(getLocalName() + ": starting to work!");
    }

    public Position makeDecision(Position stationPosition) {
        double distance = Utility.getEuclideanDistance(destination, stationPosition); // Distance of nearest station to
                                                                                      // original destination;
        double weather = Math.random();
        double distanceWeight = Math.random() * (1 - 0.65) + 0.65;
        double weatherWeight = 1 - distanceWeight;
        double likelihoodDistance = Math.min(150 / (distance + 1), 1.0);
        double likelihood = likelihoodDistance * distanceWeight + weather * weatherWeight;
        double random = Math.random();
        return random <= likelihood ? stationPosition : destination;
    }

    public void takeDown() {
        System.out.println(getLocalName() + ": done working.");
    }
}
