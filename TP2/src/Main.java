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
        // RepastLauncher launcher = new RepastLaun;
    }
}
