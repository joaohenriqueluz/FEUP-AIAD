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
        addBehaviour(new ClientTickerBehaviour(this, 5000));
        // addBehaviour(new RequestOther(this));
        System.out.println(getLocalName() + ": starting to work!");
    }

    public Position makeDecision(Position stationPosition) {

        if(stationPosition.equals(this.destination)){
         return destination;
        }
        
        double destinationToStation = Utility.getEuclideanDistance(destination, stationPosition); // Distance of nearest
                                                                                                  // station to original
                                                                                                  // destination;
        // a ----b----c
        // b--a------c
        // a----c-----b
        // For money purposes
        double positionToDestination = Utility.getEuclideanDistance(destination, this.position);
        double positionToStation = Utility.getEuclideanDistance(stationPosition, this.position);
        // Anda mais de scooter
        double moneyLikelihood = 1;      
        System.out.println("positionToDestination is " + positionToDestination + "\npositionToStation is " + positionToStation);

        if (positionToDestination < positionToStation) {
            System.out.println("Price is " + destinationToStation * scooterPriceRate);
            if (monetaryIncentive < destinationToStation * scooterPriceRate) {
                moneyLikelihood =  Math.random() * 0.3;
            } else {
                moneyLikelihood = Math.random() * (1 - 0.3) + 0.3;
            }
        }
        System.out.println("moneyLikelihood: "+ moneyLikelihood);
        // Faz o resto a pe
        System.out.println("Monetary incentive is " + monetaryIncentive);
        System.out.println("incentive is " + monetaryIncentive);

        double weather = Math.random();
        double distanceWeight = Math.random() * (0.9 - 0.50) + 0.50;
        double moneyWeight = Math.random() * (0.9 - distanceWeight) + 0.1;
        double weatherWeight = 1 - distanceWeight - moneyWeight;
        double likelihoodDistance = Math.min(100 / (destinationToStation + 1), 1.0);
        double likelihood = likelihoodDistance * distanceWeight + moneyLikelihood * moneyWeight
                + weather * weatherWeight;
        double random = Math.random();
        System.out.println("distanceW : " + distanceWeight + " moneyW: " + moneyWeight + " weatherW: " + weatherWeight
                + "  Total : " + (distanceWeight + moneyWeight + weatherWeight));
        return random <= likelihood ? stationPosition : destination;
    }

    public void takeDown() {
        System.out.println(getLocalName() + ": done working.");
    }
}
