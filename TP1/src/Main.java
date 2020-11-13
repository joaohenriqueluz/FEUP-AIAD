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
        Boolean poiFlag = false;
        double monetaryIncentive = 0;
        double staffTravelCost = 0;
        double scooterPriceRate = 0;

        if (args.length > 4 || args.length < 3) {
            System.err.println(
                    "Incorrect command. Try: java Main [-p] <monetary_incentive> <staff_travel_cost> <scooter_price_rate>");
            return;
        }

        if (args.length == 4) {
            if (args[0].equals("-p")) {
                poiFlag = true;
                monetaryIncentive = Double.parseDouble(args[1]);
                staffTravelCost = Double.parseDouble(args[2]);
                scooterPriceRate = Double.parseDouble(args[3]);
            } else {
                System.err.println(
                        "Incorrect command. Try: java Main [-p] <monetary_incentive> <staff_travel_cost> <scooter_price_rate>");
                return;
            }
        } else {
            monetaryIncentive = Double.parseDouble(args[0]);
            staffTravelCost = Double.parseDouble(args[1]);
            scooterPriceRate = Double.parseDouble(args[2]);
        }

        Runtime rt = Runtime.instance();
        Profile p1 = new ProfileImpl();
        ContainerController mainContainer = rt.createMainContainer(p1);

        try {

            AgentController company = mainContainer.acceptNewAgent("company",
                    new CompanyAgent(1, "company_0", poiFlag, monetaryIncentive, staffTravelCost, scooterPriceRate));
            company.start();

            for (int i = 0; i < 1; i++) {
                String name = "electricScooter_" + i;
                AgentController electricScooter = mainContainer.acceptNewAgent(name,
                        new ElectricScooterAgent(name, new Position()));
                electricScooter.start();
            }

            for (int i = 0; i < 1; i++) {
                String name = "client_" + i;
                AgentController client = mainContainer.acceptNewAgent(name, new ClientAgent(name));
                client.start();
            }

            for (int i = 0; i < 1; i++) {
                String name = "worker_" + i;
                AgentController worker = mainContainer.acceptNewAgent(name, new WorkerAgent(name));
                worker.start();
            }

        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }

}
