import sajas.core.Agent;
import sajas.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sajas.proto.SSResponderDispatcher;
import uchicago.src.sim.analysis.DataRecorder;
import uchicago.src.sim.space.BagCell;
import uchicago.src.sim.space.Multi2DGrid;

import java.util.ArrayList;

public class CompanyAgent extends Agent {

    private ArrayList<Position> chargingStationPositions;
    private String companyName;
    private YellowPagesService yellowPagesService;
    private int numberOfTrips;
    private int numberOfSuccessfulTrips;
    private double staffTravelCost;
    private double scooterPriceRate;
    private double monetaryIncentive;
    private double netIncome;
    private double operationCosts;
    private double totalScooterDistance;
    private double totalWorkerDistance;
    Multi2DGrid space;
    private DataRecorder recorder;

    public CompanyAgent(int numberOfStations, String name, Boolean stationAtPOIs, Double monetaryIncentive,
            Double staffTravelCost, Double scooterPriceRate, Multi2DGrid space, DataRecorder recorder) {
        this.companyName = name;
        this.numberOfSuccessfulTrips = 0;
        this.numberOfTrips = 0;
        this.netIncome = 0;
        this.operationCosts = 0;
        this.monetaryIncentive = monetaryIncentive;
        this.scooterPriceRate = scooterPriceRate;
        this.staffTravelCost = staffTravelCost;
        this.space = space;
        this.recorder = recorder;
        chargingStationPositions = new ArrayList<Position>();

        if (stationAtPOIs) {
            chargingStationPositions = Utility.placeStationsAtPOIs(numberOfStations);
        } else {
            for (int i = 0; i < numberOfStations; i++) {
                chargingStationPositions.add(new Position());
            }
        }
        for (Position station : chargingStationPositions) {
            BagCell cell = (BagCell) space.getCellAt(station.getX(), station.getY());
            if (cell == null) {
                cell = new BagCell();
                cell.add(station);
                space.putObjectAt(station.getX(), station.getY(), cell); // not multi-space (only 1 agent per cell)
            } else {
                cell.add(this);
            }
        }
    }

    public ArrayList<Position> getChargingStationPositions() {
        return chargingStationPositions;
    }

    public YellowPagesService getYellowPagesService() {
        return this.yellowPagesService;
    }

    public void setYellowPagesService(YellowPagesService yellowPagesService) {
        this.yellowPagesService = yellowPagesService;
    }

    public int getNumberOfSuccessfulTrips() {
        return this.numberOfSuccessfulTrips;
    }

    public synchronized void updateNumberOfSuccessfulTrips(int numberOfSuccessfulTrips) {
        this.numberOfSuccessfulTrips += numberOfSuccessfulTrips;
    }

    public int getNumberOfTrips() {
        return this.numberOfTrips;
    }

    public synchronized void updateNumberOfTrips(int numberOfTrips) {
        this.numberOfTrips += numberOfTrips;
    }

    public double getScooterPriceRate() {
        return this.scooterPriceRate;
    }

    public double getMonetaryIncentive() {
        return this.monetaryIncentive;
    }

    public double getStaffTravelCost() {
        return this.staffTravelCost;
    }

    public double getNetIncome() {
        return this.netIncome;
    }

    public synchronized void updateNetIncome(double distance) {
        this.netIncome += distance * scooterPriceRate;
        setTotalScooterDistance(totalScooterDistance + distance);
        updateNumberOfTrips(1);
    }

    public synchronized void updateNetIncomeWithoutIncentive(double distance) {
        this.netIncome += distance * scooterPriceRate - monetaryIncentive;
        setTotalScooterDistance(totalScooterDistance + distance);
        updateNumberOfSuccessfulTrips(1);
        updateNumberOfTrips(1);
    }

    public double getOperationCosts() {
        return this.operationCosts;
    }

    public synchronized void updateOperationCosts(double distance) {
        setTotalWorkerDistance(totalWorkerDistance + distance);
        this.operationCosts += distance * staffTravelCost;
    }

    public Position getNearestStation(Position destination) {
        Position nearest = null;
        for (Position position : chargingStationPositions) {
            if (nearest == null) {
                nearest = position;
            } else if (Utility.getEuclideanDistance(nearest, destination) > Utility.getEuclideanDistance(position,
                    destination)) {
                nearest = position;
            }
        }
        return nearest;
    }

    public double getAverageIncomePerTrip() {
        if (numberOfTrips == 0) {
            return 0;
        } else {
            return ((double) netIncome) / numberOfTrips;
        }
    }

    public double getAverageOperationCostPerTrip() {
        if (numberOfTrips == 0) {
            return 0;
        } else {
            return ((double) operationCosts) / numberOfTrips;
        }
    }

    public double getAverageProfitPerTrip() {
        if (numberOfTrips == 0) {
            return 0;
        } else {
            return (((double) netIncome) - operationCosts) / numberOfTrips;
        }
    }

    public double getProfit() {
        return netIncome - operationCosts;
    }

    public DataRecorder getRecorder() {
        return this.recorder;
    }

    public void setRecorder(DataRecorder recorder) {
        this.recorder = recorder;
    }

    public double getTotalScooterDistance() {
        return this.totalScooterDistance;
    }

    public void setTotalScooterDistance(double totalScooterDistance) {
        this.totalScooterDistance = totalScooterDistance;
    }

    public double getTotalWorkerDistance() {
        return this.totalWorkerDistance;
    }

    public void setTotalWorkerDistance(double totalWorkerDistance) {
        this.totalWorkerDistance = totalWorkerDistance;
    }

    public void printTripsInfo() {
        System.out.println("----------------------------------------------------------");
        System.out.println("Number of successful trips: " + numberOfSuccessfulTrips);
        System.out.println("Number of total trips: " + numberOfTrips);
        double percentageSuccessful = 0.0;
        double averageIncomePerTrip = netIncome / numberOfTrips;
        double averageOperationCostPerTrip = operationCosts / numberOfTrips;
        String roundedPercentage = "";
        if (numberOfTrips != 0) {
            percentageSuccessful = numberOfSuccessfulTrips / (double) numberOfTrips * 100;
            roundedPercentage = String.format("%.2f", percentageSuccessful);
        }
        System.out.println("Percentage of successful trips: " + roundedPercentage + "%");
        System.out.println("Average income per trip: " + averageIncomePerTrip);
        System.out.println("Average operation cost per trip: " + averageOperationCostPerTrip);
        System.out.println("Average profit per trip: " + (averageIncomePerTrip - averageOperationCostPerTrip));
        System.out.println("----------------------------------------------------------");
    }

    public Position getRandomStation() {
        int i = (int) Math.floor(Math.random() * (chargingStationPositions.size() - 1));
        return chargingStationPositions.get(i);
    }

    public void setup() {
        yellowPagesService = new YellowPagesService(this, "company", companyName);
        yellowPagesService.register();
        MessageTemplate template = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
        addBehaviour(new SSResponderDispatcher(this, template) {
            @Override
            protected Behaviour createResponder(ACLMessage request) {
                return new CompanyRequestResponder(((CompanyAgent) this.getAgent()), request);
            }
        });
        // addBehaviour(new CompanyRequestResponder(this,
        // MessageTemplate.MatchPerformative(ACLMessage.REQUEST)));
        System.out.println("** " + getLocalName() + ": starting to work! **");
    }

    public void takeDown() {
        System.out.println("** " + getLocalName() + ": done working. **");
        this.recorder.writeToFile();
    }

    public ArrayList<StationDrawableAgent> getStationDrawblesAgents() {
        ArrayList<StationDrawableAgent> array = new ArrayList<StationDrawableAgent>();
        for (Position stationPosition : chargingStationPositions) {
            array.add(new StationDrawableAgent(stationPosition, space));
        }
        return array;
    }
}
