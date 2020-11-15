import java.lang.Math;
import java.util.ArrayList;
import java.util.Arrays;
import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class Utility {
    private static ArrayList<Position> POIs = new ArrayList<Position>(
            Arrays.asList(new Position(), new Position(), new Position(), new Position(), new Position(),
                    new Position(), new Position(), new Position(), new Position(), new Position(), new Position(),
                    new Position(), new Position(), new Position(), new Position()));

    public static double getEuclideanDistance(Position p1, Position p2) {
        return Math.sqrt(Math.pow(p1.getX() - p2.getX(), 2) + Math.pow(p1.getY() - p2.getY(), 2));
    }

    public static Position parsePosition(String message) {
        String[] coordenates = message.split(",");
        int x = Integer.parseInt(coordenates[0]);
        int y = Integer.parseInt(coordenates[1]);
        return (new Position(x, y));
    }

    public static Position parseMessageWithPosition(String message) {
        String[] tokens = message.split("=>");
        String values = tokens[1];
        return parsePosition(values);
    }

    public static ArrayList<String> parseMessage(String message) {
        ArrayList<String> parsed = new ArrayList<String>();
        String[] tokens = message.split("=>");
        parsed.add(tokens[0]);
        if (tokens.length > 1) {
            String values = tokens[1];
            String[] arguments = values.split("--");
            for (String argument : arguments) {
                parsed.add(argument);
            }
        }
        return parsed;
    }

    public static Position getDestination(Position startPosition) {
        double r = Math.random();
        Position destination;
        if (r < 0.5 && POIs.size() != 1) {
            destination = getRandomPOI();
            while (destination.equals(startPosition)) {
                destination = getRandomPOI();
            }
            return destination;
        } else {
            destination = new Position();
            while (destination.equals(startPosition)) {
                destination = new Position();
            }
            return destination;
        }
    }

    public static ArrayList<Position> placeStationsAtPOIs(int length) {
        ArrayList<Position> positions = new ArrayList<Position>();
        Position position;
        for (int i = 0; i < length; i++) {
            if (i < POIs.size()) {
                positions.add(POIs.get(i));
            } else {
                position = new Position();
                positions.add(position);
            }
        }
        return positions;
    }

    public static Position getPOICoordenates(int i) {
        return POIs.get(i % POIs.size());
    }

    public static Position getRandomPOI() {
        int i = (int) Math.floor(Math.random() * (POIs.size() - 1));
        return POIs.get(i);
    }

    public static void log(Agent a, ACLMessage message) {
        System.out.println(
                a.getLocalName() + " received: [" + message.getPerformative() + "] \"" + message.getContent() + "\"");
    }

    public static ArrayList<Position> getPOIs() {
        return POIs;
    }
}