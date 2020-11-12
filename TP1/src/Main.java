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

public class Main {

    public static void main(String[] args) {
        Runtime rt = Runtime.instance();
        Profile p1 = new ProfileImpl();
        ContainerController mainContainer = rt.createMainContainer(p1);

        try {

            AgentController company = mainContainer.acceptNewAgent("company", new CompanyAgent(10, "company_0"));
            company.start();

            for (int i = 0; i < 3; i++) {
                String name = "electricScooter_" + i;
                AgentController electricScooter = mainContainer.acceptNewAgent(name,
                        new ElectricScooterAgent(name, new Position()));
                electricScooter.start();
            }

            for (int i = 0; i < 3; i++) {
                String name = "client_" + i;
                AgentController client = mainContainer.acceptNewAgent(name, new ClientAgent(name));
                client.start();
            }

            for (int i = 0; i < 3; i++) {
                String name = "worker_" + i;
                AgentController worker = mainContainer.acceptNewAgent(name, new WorkerAgent(name));
                worker.start();
            }

        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}
