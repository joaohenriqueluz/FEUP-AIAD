import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

public class ElectricScooterAgent extends Agent {

    private String scooterName;
    private Integer range;
    private Position position;
    private YellowPagesService yellowPagesService;

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
        yellowPagesService = new YellowPagesService(this, "electic-scooter", "scooterName");
        yellowPagesService.register();
        // Add behavior
        addBehaviour(new EScooterBehaviour());
    }

    class EScooterBehaviour extends Behaviour {

        // Wait for client wanted coordenates
            // Find closest charging station
            // Reply with charging station coordenates and pay rate
            // Move
            // Compare current coordenates with charging station
                // IF(station) End.
                // ELSE Contact CompanyAgent

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public void action() {
            ACLMessage msg = receive();
            if (msg != null) {
                System.out.println("recebi" + msg);
                parseClientMsg();
                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.INFORM);
                reply.setContent("Got your message!");
                send(reply);
            } else {
                block();    
            }
        }

        private void parseClientMsg() {
            
        }

		public boolean done() {
            return false;
        }
    }

}