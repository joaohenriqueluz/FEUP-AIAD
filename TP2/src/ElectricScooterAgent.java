import sajas.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import uchicago.src.sim.space.Object2DGrid;
import uchicago.src.sim.util.Random;
import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;
import java.awt.Color;

public class ElectricScooterAgent extends Agent implements Drawable{

    private String scooterName;
    private int range;
    private Position position;
    private Position tripStartPosition;
    private YellowPagesService yellowPagesService;
    private Boolean busy;
    // Display
    private Color color;
    private Object2DGrid space;

    public ElectricScooterAgent(String name, Position position, Object2DGrid space) {
        scooterName = name;
        this.position = position;
        this.tripStartPosition = position;
        busy = false;
        this.color = new Color( 255,127,127);
        this.space = space;
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