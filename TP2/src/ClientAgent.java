import sajas.core.Agent;
import uchicago.src.sim.space.Object2DGrid;
import uchicago.src.sim.util.Random;
import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;

import java.awt.Color;


public class ClientAgent extends Agent implements Drawable {

    private String clientName;
    private Position position;
    private Position destination;
    private double weatherWeight;
    private double distanceWeight;
    private YellowPagesService yellowPagesService;
    private double scooterPriceRate;
    private double monetaryIncentive;
    private Boolean busy;

    // Display
    private Color color;
    private Object2DGrid space;

    public ClientAgent(String name, Object2DGrid space) {
        this.clientName = name;
        this.position = new Position();
        this.busy = false;
        this.color = new Color( 127,127,255);
        this.space = space;
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

    public void setPosition(Position newPosition) {
        System.out.println("** " + getLocalName() + " new position is " + newPosition.toString() + " **");
        this.position = newPosition;
    }

    public Position getDestination() {
        return this.destination;
    }

    public void setDestination(Position newDestination) {
        System.out.println("** " + getLocalName() + " new destination is " + newDestination.toString() + " **");
        this.destination = newDestination;
    }

    public Boolean isBusy() {
        return this.busy;
    }

    public void setBusy(Boolean busy) {
        this.busy = busy;
    }

    public double getScooterPriceRate() {
        return this.scooterPriceRate;
    }

    public void setScooterPriceRate(double scooterPriceRate) {
        this.scooterPriceRate = scooterPriceRate;
    }

    public double getMonetaryIncentive() {
        return this.monetaryIncentive;
    }

    public void setMonetaryIncentive(double monetaryIncentive) {
        this.monetaryIncentive = monetaryIncentive;
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
        // addBehaviour(new ClientTickerBehaviour(this, 2000));
        System.out.println("** " + getLocalName() + ": starting to work! **");
    }

    public Position makeDecision(Position stationPosition) {

        if (stationPosition.equals(this.destination)) {
            return destination;
        }

        double destinationToStation = Utility.getEuclideanDistance(destination, stationPosition); // Distance of nearest
        // station to original
        // destination;
        // For money purposes
        double positionToDestination = Utility.getEuclideanDistance(destination, this.position);
        double positionToStation = Utility.getEuclideanDistance(stationPosition, this.position);
        double moneyLikelihood = 1;

        if (monetaryIncentive == 0)
            moneyLikelihood = 0;

        if (positionToDestination < positionToStation) {
            if (monetaryIncentive < destinationToStation * scooterPriceRate) {
                moneyLikelihood = Math.random() * 0.3;
            } else {
                moneyLikelihood = Math.random() * (1 - 0.3) + 0.3;
            }
        }

        double fitness = Math.random() * (1 - 0.4) + 0.4;
        double weather = Math.random();
        double distanceWeight = Math.random() * (0.8 - 0.50) + 0.50;
        double moneyWeight = Math.random() * (0.8 - distanceWeight) + 0.1;
        if (monetaryIncentive == 0)
            moneyWeight = 0;
        double weatherWeight = 1 - distanceWeight - moneyWeight;
        double likelihoodDistance = fitness * (Math.min(150 / (destinationToStation + 1), 1.0));
        double likelihood = likelihoodDistance * distanceWeight + moneyLikelihood * moneyWeight
                + weather * weatherWeight;
        double random = Math.random();
        return random <= likelihood ? stationPosition : destination;
    }

    public void takeDown() {
        System.out.println("** " + getLocalName() + ": done working. **");
    }

    public void draw(SimGraphics g) {
        g.drawFastCircle(color);
    }

    public int getY(){
        return position.getY();
    }

    public int getX(){
        return position.getX();
    }
}
