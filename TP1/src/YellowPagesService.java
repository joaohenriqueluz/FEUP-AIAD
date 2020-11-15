import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

public class YellowPagesService {

    private final String name;
    private final String type;
    private Agent agent;

    public YellowPagesService(Agent agent, String type, String name) {
        this.agent = agent;
        this.type = type;
        this.name = name;
    }

    public void register() {
        DFAgentDescription agentDescription = new DFAgentDescription();
        agentDescription.setName(this.agent.getAID());
        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType(this.type);
        serviceDescription.setName(this.name);
        agentDescription.addServices(serviceDescription);
        try {
            DFService.register(this.agent, agentDescription);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }

    public void deregister() {
        try {
            DFService.deregister(this.agent);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }

    public AID[] getAgentList(String typeToSearch) {
        AID[] agentsAIDList = new AID[0];

        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(typeToSearch);
        template.addServices(sd);

        try {
            DFAgentDescription[] results = DFService.search(this.agent, template);
            agentsAIDList = new AID[results.length];
            for (int i = 0; i < results.length; ++i) {
                agentsAIDList[i] = results[i].getName();
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        return agentsAIDList;
    }
}