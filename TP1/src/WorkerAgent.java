import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.MessageTemplate;

public class WorkerAgent extends Agent {

    private String workerName;
    private Position position;
    private Position destination;
    private YellowPagesService yellowPagesService;

    public WorkerAgent(String name) {
        workerName = name;
        position = new Position();
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
        System.out.println(getLocalName() + " new position is " + newPosition.toString());
        this.position = newPosition;
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
        // addBehaviour(new RequestOther(this));
        System.out.println(getLocalName() + ": starting to work!");
    }

    public void takeDown() {
        System.out.println(getLocalName() + ": done working.");
    }
}