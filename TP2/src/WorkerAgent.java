import sajas.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import uchicago.src.sim.space.BagCell;
import uchicago.src.sim.space.Multi2DGrid;
import uchicago.src.sim.util.Random;
import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;

import java.awt.Color;

public class WorkerAgent extends Agent implements Drawable {

    private String workerName;
    private Position position;
    private Position destination;
    private Boolean busy;
    private YellowPagesService yellowPagesService;

    // Display
    private Color color;
    private Multi2DGrid space;

    public WorkerAgent(String name, Multi2DGrid space) {
        workerName = name;
        position = new Position();
        busy = false;
        this.color = new Color(127, 255, 127);
        this.space = space;
    }

    public WorkerAgent(String name, Position position, Multi2DGrid space) {
        workerName = name;
        this.position = position;
        busy = false;
        this.color = new Color(127, 255, 127);
        this.space = space;
    }

    public String getWorkerName() {
        return this.workerName;
    }

    public void setWorkerName(String newName) {
        this.workerName = newName;
    }

    public Position getPosition() {
        return this.position;
    }

    public void setPosition(Position newPosition) {
        System.out.println("** " + getLocalName() + " new position is " + newPosition.toString() + " **");
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

    public Boolean isBusy() {
        return this.busy;
    }

    public void setBusy(Boolean busy) {
        this.busy = busy;
    }

    public YellowPagesService getYellowPagesService() {
        return this.yellowPagesService;
    }

    public void setYellowPagesService(YellowPagesService yellowPagesService) {
        this.yellowPagesService = yellowPagesService;
    }

    public void setup() {
        yellowPagesService = new YellowPagesService(this, "worker", workerName);
        yellowPagesService.register();
        addBehaviour(new WorkerContractResponder(this, MessageTemplate.MatchPerformative(ACLMessage.CFP)));
        System.out.println("** " + getLocalName() + ": starting to work! **");
    }

    public void takeDown() {
        System.out.println("** " + getLocalName() + ": done working. **");
    }

    public void draw(SimGraphics g) {
        g.drawFastCircle(color);
    }

    public int getY() {
        return position.getY();
    }

    public int getX() {
        return position.getX();
    }

}