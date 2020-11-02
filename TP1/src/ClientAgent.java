import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

public class ClientAgent extends Agent {

	private String clientName;
	private Position position;
	private YellowPagesService yellowPagesService;

	public ClientAgent(String name){
		clientName = name;
		position = new Position();
    }

	public String getClientName(){
		return this.clientName;
	}

	public void setClientName(String newName){
		this.clientName = newName;
	}

	public Position getPosition(){
		return this.position;
	}

	public void setPosition(Position newPosition){
		this.position = newPosition;
	}
	
	public YellowPagesService getYellowPagesService() {
		return this.yellowPagesService;
	}

	public void setYellowPagesService(YellowPagesService yellowPagesService) {
		this.yellowPagesService = yellowPagesService;
	}
	
	public void setup() {
		yellowPagesService = new YellowPagesService(this, "client", clientName );
		yellowPagesService.register();
		addBehaviour(new RequestESBehaviour(this, new ACLMessage(ACLMessage.REQUEST)));
		// addBehaviour(new RequestOther(this));
		System.out.println(getLocalName() + ": starting to work!");
	}
	
	public void takeDown() {
		System.out.println(getLocalName() + ": done working.");
	}	
}
