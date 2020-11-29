import sajas.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ElectricScooterAgent extends Agent {

    private String scooterName;
    private int range;
    private Position position;
    private Position tripStartPosition;
    private YellowPagesService yellowPagesService;
    private Boolean busy;

    public ElectricScooterAgent(String name, Position position) {
        scooterName = name;
        this.position = position;
        this.tripStartPosition = position;
        busy = false;
    }

    public String getScooterName() {
        return this.scooterName;
    }

    public void setScooterName(String scooterName) {
        this.scooterName = scooterName;
    }

    public int getRange() {
        return this.range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public Boolean isBusy() {
        return this.busy;
    }

    public void setBusy(Boolean busy) {
        this.busy = busy;
    }

    public Position getPosition() {
        return this.position;
    }

    public void setPosition(Position position) {
        System.out.println("** " + getLocalName() + " new position is " + position.toString() + " **");
        this.position = position;
    }

    public Position getTripStartPosition() {
        return this.tripStartPosition;
    }

    public void setTripStartPosition(Position tripStartPosition) {
        this.tripStartPosition = tripStartPosition;
    }

    public YellowPagesService getYellowPagesService() {
        return this.yellowPagesService;
    }

    public void setYellowPagesService(YellowPagesService yellowPagesService) {
        this.yellowPagesService = yellowPagesService;
    }

    public void setup() {
        // Register in Yellow Page service
        yellowPagesService = new YellowPagesService(this, "electic-scooter", scooterName);
        yellowPagesService.register();
        // Add behavior
        // addBehaviour(new ElecticScooterREResponder(this,
        // MessageTemplate.MatchPerformative(ACLMessage.REQUEST)));
        addBehaviour(new CompanyScooterContractResponder(this, MessageTemplate.MatchPerformative(ACLMessage.CFP)));
    }

}