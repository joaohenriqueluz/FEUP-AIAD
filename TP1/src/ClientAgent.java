import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

public class ClientAgent extends Agent {
	private YellowPagesService yellowPagesService;
	
	public YellowPagesService getYellowPagesService() {
		return this.yellowPagesService;
	}

	public void setYellowPagesService(YellowPagesService yellowPagesService) {
		this.yellowPagesService = yellowPagesService;
	}
	
	public void setup() {
		yellowPagesService = new YellowPagesService(this, "client", "client" );
        yellowPagesService.register();
		addBehaviour(new RequestESBehaviour(this));
		System.out.println(getLocalName() + ": starting to work!");
	}
	
	public void takeDown() {
		System.out.println(getLocalName() + ": done working.");
	}	
}
