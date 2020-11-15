import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import jdk.jshell.execution.Util;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        Boolean poiFlag = false;
        double monetaryIncentive = 0;
        double staffTravelCost = 0;
        double scooterPriceRate = 0;

        int numberOfClients = 0;
        int numberOfScooters = 0;
        int numberOfWorkers = 0;
        int argIndex = 0;

        if (args.length > 8 || args.length < 6) {
            System.err.println(
                    "Incorrect command. Try: java Main [-p] [-v] <monetary_incentive> <staff_travel_cost> <scooter_price_rate> <number_of_clients> <number_of_scooter> <number_of_workers> ");
            return;
        }

        for (String arg : args) {
            if (arg.equals("-p")) {
                poiFlag = true;
                argIndex++;
            } else if (arg.equals("-v")) {
                Utility.setVerbose(true);
                argIndex++;
            }
        }

        if ((args.length - argIndex) != 6) {
            System.err.println(
                    "Incorrect command. Try: java Main [-p] [-v] <monetary_incentive> <staff_travel_cost> <scooter_price_rate> <number_of_clients> <number_of_scooter> <number_of_workers> ");
            return;
        }

        try {
            monetaryIncentive = Double.parseDouble(args[argIndex]);
            staffTravelCost = Double.parseDouble(args[argIndex + 1]);
            scooterPriceRate = Double.parseDouble(args[argIndex + 2]);

            numberOfClients = Integer.parseInt(args[argIndex + 3]);
            numberOfScooters = Integer.parseInt(args[argIndex + 4]);
            numberOfWorkers = Integer.parseInt(args[argIndex + 5]);
        } catch (Exception e) {
            System.err.println(
                    "Incorrect command. Try: java Main [-p] [-v] <monetary_incentive> <staff_travel_cost> <scooter_price_rate> <number_of_clients> <number_of_scooter> <number_of_workers> ");
            return;
        }

        Runtime rt = Runtime.instance();
        Profile p1 = new ProfileImpl();
        ContainerController mainContainer = rt.createMainContainer(p1);

        try {
            CompanyAgent companyAgent = new CompanyAgent(10, "company_0", poiFlag, monetaryIncentive, staffTravelCost,
                    scooterPriceRate);
            AgentController company = mainContainer.acceptNewAgent("company", companyAgent);
            company.start();

            ArrayList<Position> stationPositions = companyAgent.getChargingStationPositions();
            for (int i = 0; i < numberOfScooters; i++) {
                String name = "electricScooter_" + i;
                AgentController electricScooter = mainContainer.acceptNewAgent(name,
                        new ElectricScooterAgent(name, stationPositions.get(i % stationPositions.size())));
                electricScooter.start();
            }

            for (int i = 0; i < numberOfClients; i++) {
                String name = "client_" + i;
                AgentController client = mainContainer.acceptNewAgent(name, new ClientAgent(name));
                client.start();
            }

            for (int i = 0; i < numberOfWorkers; i++) {
                String name = "worker_" + i;
                if (poiFlag) {
                    AgentController worker = mainContainer.acceptNewAgent(name,
                            new WorkerAgent(name, Utility.getPOICoordenates(i)));
                    worker.start();
                } else {
                    AgentController worker = mainContainer.acceptNewAgent(name, new WorkerAgent(name));
                    worker.start();
                }

            }

        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }

}
