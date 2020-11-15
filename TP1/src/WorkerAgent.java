import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class WorkerAgent extends Agent {

    private String workerName;
    private Position position;
    private Position destination;
    private Boolean busy;
    private YellowPagesService yellowPagesService;

    public WorkerAgent(String name) {
        workerName = name;
        position = new Position();
        busy = false;
    }

    public WorkerAgent(String name, Position position) {
        workerName = name;
        this.position = position;
        busy = false;
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
        this.position = newPosition;
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
}