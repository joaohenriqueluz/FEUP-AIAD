import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

class RequestESBehaviour extends Behaviour {
    private int n = 0;
    ClientAgent client;

    public RequestESBehaviour(ClientAgent client){
        this.client = client;
    }
    
    public void action() {
        AID[] scooterAgents = client.getYellowPagesService().getAgentList("electic-scooter");
        System.out.println(scooterAgents);
        if (scooterAgents != null) {
            for (AID aid : scooterAgents) {
               String[] args = {"my position", "100", "100"};
               Utility.sendRequest(
                       client, aid, (ACLMessage.REQUEST), "ask-for-scooter", Utility.arrayToString(args));
                       System.out.println(++n + " I am doing something!");
           }
        }
   }

    public boolean done() {
        return false;
    }
}