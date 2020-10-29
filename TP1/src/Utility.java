import java.lang.Math; 
import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class Utility {
    public static double getEuclideanDistance(Position p1, Position p2) {
        return Math.sqrt(Math.pow(p1.getX() - p2.getX(), 2) + Math.pow(p1.getY() - p2.getY(), 2));
    }

    public static void sendRequest(Agent senderAgent, AID receiverAgent, int messageType, String conversationID, String content) {
        ACLMessage req = new ACLMessage(messageType);
        req.setConversationId(conversationID);
        req.addReceiver(receiverAgent);
        req.setContent(content);
        senderAgent.send(req);
    }

    public static String arrayToString(String[] array) {
        String message = new String();
        for (int i = 0; i < array.length; i++) {
            message += array[i];
            if (i < array.length-1)
                message += ",";
        }

        return message;
    }
}