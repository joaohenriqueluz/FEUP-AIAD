import jade.core.ProfileImpl;
import jade.core.Profile;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.ContainerController;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.ArrayList;

public class Main {

    private ArrayList<Position> mapCoordinates;
    private ArrayList<Integer> coordinatesWeight;

    public static void main(String[] args) {
        // Get a JADE runtime
        Runtime rt = Runtime.instance();

        // Create the main container
        Profile p1 = new ProfileImpl();
        // p1.setParameter(...); // optional
        ContainerController mainContainer = rt.createMainContainer(p1);

        // Launch agent
        // Object[] agentArgs = new Object[...];
        // AgentController ac2 = container.createNewAgent("name2", "jade.core.Agent",
        // agentArgs);
        // ac2.start();

        try {

            AgentController company = mainContainer.acceptNewAgent("company", new CompanyAgent(10,"company_0"));
            company.start();

            for (int i = 0; i < 3; i++) {
                String name = "electricScooter_" + i;
                AgentController electricScooter = mainContainer.acceptNewAgent(name, new ElectricScooterAgent(name, new Position()));
                electricScooter.start();
            }

            for (int i = 0; i < 1; i++) {
                String name = "client_" + i;
                AgentController client = mainContainer.acceptNewAgent(name, new ClientAgent(name));
                client.start();
            }
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}
