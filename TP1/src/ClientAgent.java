import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

public class ClientAgent extends Agent {

	private String clientName;
	private Position position;
	private Position destination;
	private YellowPagesService yellowPagesService;

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

	public YellowPagesService getYellowPagesService() {
		return this.yellowPagesService;
	}

	public void setYellowPagesService(YellowPagesService yellowPagesService) {
		this.yellowPagesService = yellowPagesService;
	}

	public void setup() {
		yellowPagesService = new YellowPagesService(this, "client", clientName);
		yellowPagesService.register();
		addBehaviour(new CompanyClientRequestInitiator(this, new ACLMessage(ACLMessage.REQUEST)));
		// addBehaviour(new RequestOther(this));
		System.out.println(getLocalName() + ": starting to work!");
	}

	public void takeDown() {
		System.out.println(getLocalName() + ": done working.");
	}
}
