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

        int numberOfClients = 0;
        int numberOfScooters = 0;
        int numberOfWorkers = 0;

        if (args.length > 7 || args.length < 6) {
            System.err.println(
                    "Incorrect command. Try: java Main [-p] <monetary_incentive> <staff_travel_cost> <scooter_price_rate> <number_of_clients> <number_of_scooter> <number_of_workers> ");
            return;
        }

        if (args.length == 7) {
            if (args[0].equals("-p")) {
                poiFlag = true;
                monetaryIncentive = Double.parseDouble(args[1]);
                staffTravelCost = Double.parseDouble(args[2]);
                scooterPriceRate = Double.parseDouble(args[3]);

                numberOfClients = Integer.parseInt(args[4]);
                numberOfScooters = Integer.parseInt(args[5]);
                numberOfWorkers = Integer.parseInt(args[6]);
            } else {
                System.err.println(
                        "Incorrect command. Try: java Main [-p] <monetary_incentive> <staff_travel_cost> <scooter_price_rate> <number_of_clients> <number_of_scooter> <number_of_workers> ");
                return;
            }
        } else {
            monetaryIncentive = Double.parseDouble(args[0]);
            staffTravelCost = Double.parseDouble(args[1]);
            scooterPriceRate = Double.parseDouble(args[2]);

            numberOfClients = Integer.parseInt(args[3]);
            numberOfScooters = Integer.parseInt(args[4]);
            numberOfWorkers = Integer.parseInt(args[5]);
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
                if(poiFlag){
                    AgentController worker = mainContainer.acceptNewAgent(name, new WorkerAgent(name, Utility.getPOICoordenates(i)));
                    worker.start();
                }
                else{
                    AgentController worker = mainContainer.acceptNewAgent(name, new WorkerAgent(name));
                    worker.start();
                }

            }

        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }

}
