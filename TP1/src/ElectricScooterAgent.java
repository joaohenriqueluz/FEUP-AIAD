import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ElectricScooterAgent extends Agent {

	private String scooterName;
	private Integer range;
	private Position position;
	private YellowPagesService yellowPagesService;

	public ElectricScooterAgent(String name, Position position) {
		scooterName = name;
		this.position = position;
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

	public Position getPosition() {
		return this.position;
	}

	public void setPosition(Position position) {
		this.position = position;
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