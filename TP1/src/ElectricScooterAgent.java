import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ElectricScooterAgent extends Agent {

    private String scooterName;
    private Integer range;
    private Position position;
    private YellowPagesService yellowPagesService;
    private Boolean busy;

    public ElectricScooterAgent(String name, Position position) {
        scooterName = name;
        this.position = position;
        busy = false;
    }

    public String getScooterName() {
        return this.scooterName;
    }

    public void setScooterName(String scooterName) {
        this.scooterName = scooterName;
    }

    public Integer getRange() {
        return this.range;
    }

    public void setRange(Integer range) {
        this.range = range;
    }

    public Boolean isBusy() {
		System.out.println(this.getLocalName() + " busy = " + this.busy);
        return this.busy;
    }

    public void setBusy(Boolean busy) {
		System.out.println(this.getLocalName() + " busy = " + this.busy);
        this.busy = busy;
    }

    public Position getPosition() {
        return this.position;
    }

    public void setPosition(Position position) {
        this.position = position;
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
        System.out.println("E-Scooter setup");

        addBehaviour(new CompanyScooterContractResponder(this, MessageTemplate.MatchPerformative(ACLMessage.CFP)));
    }

}