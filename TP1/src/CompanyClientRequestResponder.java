import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREResponder;
import jade.lang.acl.MessageTemplate;

class CompanyClientRequestResponder extends AchieveREResponder {
    CompanyAgent company;

    public CompanyClientRequestResponder(CompanyAgent company, MessageTemplate mt) {
        super(company, mt);
        this.company = company;
    }

    protected ACLMessage handleRequest(ACLMessage request) {
        parseRequest(request.getContent());
        ACLMessage reply = request.createReply();
        reply.setContent("Recebi, xuxu!");
        reply.setPerformative(ACLMessage.AGREE);
        return reply;
    }

    public void parseRequest(String request){
        System.out.println(this.company.getLocalName() + ": " + request);
        String[] tokens = request.split(":");
        switch(tokens[0]){
            case "GET-SCOOTER":
                System.out.println(tokens);
                parseGetScooter(tokens);
                break;
            default:
                break;
        }
    }

    private void parseGetScooter(String[] tokens){
        String[] coordenates = tokens[1].split(",");
        Position position = new Position(Integer.parseInt(coordenates[0]),Integer.parseInt(coordenates[1]));
        ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
        message.setContent("GET-SCOOTER:" + coordenates[0]+ ","+ coordenates[1]);
        AID[] scooterAgents = company.getYellowPagesService().getAgentList("electic-scooter");
        System.out.println(scooterAgents);
        if (scooterAgents != null) {
            for (AID scooterId : scooterAgents) {
                message.addReceiver(scooterId);
           }
        }
        company.send(message);
    }
    // protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage
    // response) {
    // ACLMessage result = request.createReply();
    // // ...
    // return result;
    // }
}