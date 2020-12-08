import sajas.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import uchicago.src.sim.space.BagCell;
import uchicago.src.sim.space.Multi2DGrid;
import uchicago.src.sim.util.Random;
import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;
import java.awt.Color;

public class ElectricScooterAgent extends Agent implements Drawable {

    private String scooterName;
    private int range;
    private Position position;
    private Position tripStartPosition;
    private YellowPagesService yellowPagesService;
    private Boolean busy;
    // Display
    private Color color;
    private Multi2DGrid space;

    public ElectricScooterAgent(String name, Position position, Multi2DGrid space) {
        scooterName = name;
        this.position = position;
        this.tripStartPosition = position;
        busy = false;
        this.color = new Color(255, 127, 127);
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

    public synchronized Boolean isBusy() {
        return this.busy;
    }

    public synchronized void setBusy(Boolean busy) {
        this.busy = busy;
    }

    public Position getPosition() {
        return this.position;
    }

    public void setPosition(Position newPosition) {
        System.out.println("** " + getLocalName() + " new position is " + position.toString() + " **");
        BagCell cell = (BagCell) space.getCellAt(this.position.getX(), this.position.getY());
        cell.remove(this);
        space.putObjectAt(this.position.getX(), this.position.getY(), cell);
        this.position = newPosition;
        cell = (BagCell) space.getCellAt(this.position.getX(), this.position.getY());
        if (cell == null) {
            cell = new BagCell();
            cell.add(this);
            space.putObjectAt(this.position.getX(), this.position.getY(), cell); // not multi-space (only 1 agent per
                                                                                 // cell)
        } else {
            cell.add(this);
        }
         
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
        g.drawFastOval(color);
    }

    public int getY() {
        return position.getY();
    }

    public int getX() {
        return position.getX();
    }

}