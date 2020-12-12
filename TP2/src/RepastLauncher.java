import java.awt.Dimension;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

import sajas.sim.repast3.Repast3Launcher;
import sajas.core.Agent;
import sajas.core.Runtime;
import sajas.wrapper.AgentController;
import sajas.wrapper.ContainerController;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.StaleProxyException;
import uchicago.src.sim.analysis.DataRecorder;
import uchicago.src.sim.analysis.DataSource;
import uchicago.src.sim.analysis.OpenSequenceGraph;
import uchicago.src.sim.analysis.Sequence;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Object2DDisplay;
import uchicago.src.sim.space.BagCell;
import uchicago.src.sim.space.Multi2DGrid;

public class RepastLauncher extends Repast3Launcher {

    Boolean poiFlag = false;
    Boolean verboseFlag = false;
    double monetaryIncentive = 0;
    double staffTravelCost = 0;
    double scooterPriceRate = 0;
    int numberOfClients = 1;
    int numberOfScooters = 1;
    int numberOfWorkers = 1;
    private double weatherConditionsMax = 1.0;
    private double weatherConditionsMin = 0.0;
    private double clientsFitnessMax = 1.0; // The clients' physical aptitude maximum value
    private double clientsFitnessMin = 0.0; // The clients' physical aptitude minimum value

    public static Multi2DGrid space;
    private DisplaySurface dsurf;
    public static int spaceSize = 150;
    private DataRecorder recorder;
    private Boolean showPlots = false;
    private Boolean showDisplay = false;

    private ArrayList<Agent> agentList;
    private CompanyAgent companyAgent;

    public static void main(String[] args) {

        boolean batchMode = false;
        SimInit init = new SimInit();
        RepastLauncher model = new RepastLauncher();

        ArrayList<String> allARgs = new ArrayList<String>(Arrays.asList(args));
        if (allARgs.size() > 0) {
            int argIndex = 0;
            if (allARgs.contains("-b")) {
                batchMode = true;
                argIndex++;
            }
            if (allARgs.contains("-v")) {
                model.setVerboseFlag(true);
                argIndex++;
            }
            if (allARgs.contains("-p")) {
                model.setPoiFlag(true);
                argIndex++;
            }

            try {
                model.setMonetaryIncentive(Double.parseDouble(args[argIndex]));
                model.setStaffTravelCost(Double.parseDouble(args[argIndex + 1]));
                model.setScooterPriceRate(Double.parseDouble(args[argIndex + 2]));
                model.setNumberOfClients(Integer.parseInt(args[argIndex + 3]));
                model.setNumberOfScooters(Integer.parseInt(args[argIndex + 4]));
                model.setNumberOfWorkers(Integer.parseInt(args[argIndex + 5]));
            } catch (Exception e) {
                System.err.println(
                        "Incorrect command. Try:\n\tjava -cp (...) src/RepasLauncher.java [-b] [-p] [-v] <monetary_incentive> <staff_travel_cost> <scooter_price_rate> <number_of_clients> <number_of_scooter> <number_of_workers> (for batch mode)"
                                + "\n\tjava -cp (...) src/RepasLauncher.java (for GUI mode)");
                return;
            }
        }
        // load model into simulation:
        init.loadModel(model, null, batchMode);
    }

    public RepastLauncher() {
        super();
        agentList = new ArrayList<Agent>();
    }

    @Override
    public void setup() {
        super.setup();
        System.out.println("setup");
        if (dsurf != null)
            dsurf.dispose();
        // dsurf = new DisplaySurface(new Dimension(spaceSize * 2, spaceSize * 2), this,
        // "Scooter Sim Display");
        dsurf = new DisplaySurface(this, "Scooter Sim Display");
        registerDisplaySurface("Scooter Sim Display", dsurf);
    }

    public String getFileName(){
        String filename = "./src/output/";
        if(poiFlag){
           filename += "POI_";
        }else{
            filename += "RANDOM_";
        }
        filename += monetaryIncentive + "_";
        filename += scooterPriceRate + "_";
        filename += staffTravelCost +".csv";
        return filename;

        // "./src/output/scooterPerformanceFitness_" + clientsFitnessMin + "-"
        //         + clientsFitnessMax + "_Weather_" + weatherConditionsMin + "-" + weatherConditionsMax + ".csv"
    }

    @Override
    public void launchJADE() {
        System.out.println("launchJADE");
        System.out.println("Launching JADE");
        Runtime rt = Runtime.instance();
        Profile p1 = new ProfileImpl();
        ContainerController mainContainer = rt.createMainContainer(p1);
        space = new Multi2DGrid(spaceSize, spaceSize, false);
        recorder = new DataRecorder(getFileName(), this);

        try {
            companyAgent = new CompanyAgent(10, "company_0", poiFlag, monetaryIncentive, staffTravelCost,
                    scooterPriceRate, space, recorder);
            AgentController company = mainContainer.acceptNewAgent("company", companyAgent);
            company.start();
            // StationDrawableAgent stationDA1 = new StationDrawableAgent(new Position(0, 0), space);
            // space.putObjectAt(stationDA1.getX(), stationDA1.getY(), stationDA1);
            // agentList.add(stationDA1);

            // StationDrawableAgent stationDA2 = new StationDrawableAgent(new Position(1, 1), space);
            // space.putObjectAt(stationDA2.getX(), stationDA2.getY(), stationDA2);
            // agentList.add(stationDA2);

            // StationDrawableAgent stationDA3 = new StationDrawableAgent(new Position((spaceSize / 2), (spaceSize / 2)),
            //         space, new Color(255, 0, 0));
            // space.putObjectAt(stationDA3.getX(), stationDA3.getY(), stationDA3);
            // agentList.add(stationDA3);

            // StationDrawableAgent stationDA4 = new StationDrawableAgent(new Position(0, (spaceSize / 2)), space,
            //         new Color(255, 0, 0));
            // space.putObjectAt(stationDA4.getX(), stationDA4.getY(), stationDA4);
            // agentList.add(stationDA4);

            // for (Agent stationDrawableAgent : companyAgent.getStationDrawblesAgents()) {
            //     agentList.add(stationDrawableAgent);
            // }

            ArrayList<Position> stationPositions = companyAgent.getChargingStationPositions();
            for (int i = 0; i < numberOfScooters; i++) {
                String name = "electricScooter_" + i;
                ElectricScooterAgent electricScooterAgent = new ElectricScooterAgent(name,
                        stationPositions.get(i % stationPositions.size()), space);
                AgentController electricScooter = mainContainer.acceptNewAgent(name, electricScooterAgent);
                agentList.add(electricScooterAgent);
                electricScooter.start();
            }

            for (int i = 0; i < numberOfClients; i++) {
                String name = "client_" + i;
                ClientAgent clientAgent = new ClientAgent(name, space);
                AgentController client = mainContainer.acceptNewAgent(name, clientAgent);
                agentList.add(clientAgent);
                client.start();
            }

            for (int i = 0; i < numberOfWorkers; i++) {
                String name = "worker_" + i;
                AgentController worker;
                WorkerAgent workerAgent;
                if (poiFlag) {
                    workerAgent = new WorkerAgent(name, Utility.getPOICoordenates(i), space);
                } else {
                    workerAgent = new WorkerAgent(name, space);
                }
                worker = mainContainer.acceptNewAgent(name, workerAgent);
                agentList.add(workerAgent);
                worker.start();
            }

        } catch (StaleProxyException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void begin() {
        super.begin();
        buildModel();
        buildDisplay();
        buildSchedule();
    }

    public void buildModel() {
        recorder.addObjectDataSource("Total Trips", new DataSource() {
            public Object execute() {
                return companyAgent.getNumberOfTrips();
            }
        });

        recorder.addObjectDataSource("Total Successful Trips", new DataSource() {
            public Object execute() {
                return companyAgent.getNumberOfSuccessfulTrips();
            }
        });
        recorder.addObjectDataSource("Successful Trips Percentage", new DataSource() {
            public Object execute() {
                return 100 * ((double) companyAgent.getNumberOfSuccessfulTrips()) / companyAgent.getNumberOfTrips();
            }
        });

        recorder.addObjectDataSource("Average Income", new DataSource() {
            public Object execute() {
                return companyAgent.getAverageIncomePerTrip();
            }
        });
        recorder.addObjectDataSource("Average Operation Cost", new DataSource() {
            public Object execute() {
                return companyAgent.getAverageOperationCostPerTrip();
            }
        });

        recorder.addObjectDataSource("Average Proft", new DataSource() {
            public Object execute() {
                return companyAgent.getAverageProfitPerTrip();
            }
        });
        recorder.addObjectDataSource("Total Profit", new DataSource() {
            public Object execute() {
                return companyAgent.getProfit();
            }
        });

        recorder.addObjectDataSource("Total Scooter Distance", new DataSource() {
            public Object execute() {
                return companyAgent.getTotalScooterDistance();
            }
        });

        recorder.addObjectDataSource("Total Worker Distance", new DataSource() {
            public Object execute() {
                return companyAgent.getTotalWorkerDistance();
            }
        });
    }

    private OpenSequenceGraph plotIncome;
    private OpenSequenceGraph plotSuccessuful;

    private void buildDisplay() {
        System.out.println("[BUILDING DISPLAY]");
        if (showDisplay) {
            // space and display surface
            Object2DDisplay display = new Object2DDisplay(space);
            display.setObjectList(agentList);
            dsurf.addDisplayableProbeable(display, "Agents Space");
            dsurf.display();
        }
        if (showPlots) {
            buildPlots();
        }
    }

    private void buildSchedule() {
        System.out.println("[BUILDING SCHEDULE]");
        if (showDisplay) {
            getSchedule().scheduleActionAtInterval(1, dsurf, "updateDisplay", Schedule.LAST);
        }
        if (showPlots) {
            getSchedule().scheduleActionAtInterval(100, plotSuccessuful, "step", Schedule.LAST);
            getSchedule().scheduleActionAtInterval(100, plotIncome, "step", Schedule.LAST);
        }
    }

    private void buildPlots() {
        System.out.println("[BUILDING PLOTS]");
        // graph
        if (plotSuccessuful != null)
            plotSuccessuful.dispose();
        plotSuccessuful = new OpenSequenceGraph("Successful Trips", this);
        plotSuccessuful.setAxisTitles("time", "% successful trips");
        // plotSuccessuful.setYRange(0, 100);
        // plotSuccessuful.addSequence("% of successful trips", new Sequence() {
        // public double getSValue() {
        // return 100 * ((double) companyAgent.getNumberOfSuccessfulTrips()) /
        // companyAgent.getNumberOfTrips();
        // }
        // });
        plotSuccessuful.addSequence("Total number of trips", new Sequence() {
            public double getSValue() {
                return companyAgent.getNumberOfTrips();
            }
        });
        plotSuccessuful.addSequence("Number of successful trips", new Sequence() {
            public double getSValue() {
                return companyAgent.getNumberOfSuccessfulTrips();
            }
        });

        plotSuccessuful.display();

        if (plotIncome != null)
            plotIncome.dispose();
        plotIncome = new OpenSequenceGraph("Income Statement", this);
        plotIncome.setAxisTitles("time", "Monetary Units");
        plotIncome.addSequence("Average Income", new Sequence() {
            public double getSValue() {
                return companyAgent.getAverageIncomePerTrip();
            }
        });

        plotIncome.addSequence("Average Trip Operation Cost", new Sequence() {
            public double getSValue() {
                return companyAgent.getAverageOperationCostPerTrip();
            }
        });

        plotIncome.addSequence("Average Profit", new Sequence() {
            public double getSValue() {
                return companyAgent.getAverageProfitPerTrip();
            }
        });

        plotIncome.display();
    }

    public void step() {
        recorder.record();
    }

    @Override
    public String[] getInitParam() {
        return new String[] { "poiFlag", "verboseFlag", "monetaryIncentive", "staffTravelCost", "scooterPriceRate",
                "numberOfClients", "numberOfScooters", "numberOfWorkers", "weatherConditionsMax",
                "weatherConditionsMin", "clientsFitnessMax", "clientsFitnessMin", "showPlots", "showDisplay" };
    }

    @Override
    public String getName() {
        return "Electric Scooter Simulation";
    }

    public double getClientsFitnessMax() {
        return clientsFitnessMax;
    }

    public void setClientsFitnessMax(double newClientsFitnessMax) {
        System.out.println("[Clients Fitness Max = " + newClientsFitnessMax + "]");
        Utility.setClientsFitnessMax(newClientsFitnessMax);
        clientsFitnessMax = newClientsFitnessMax;
    }

    public double getClientsFitnessMin() {
        return clientsFitnessMin;
    }

    public void setClientsFitnessMin(double newClientsFitnessMin) {
        System.out.println("[Clients Fitness Min = " + newClientsFitnessMin + "]");
        Utility.setClientsFitnessMin(newClientsFitnessMin);
        clientsFitnessMin = newClientsFitnessMin;
    }

    public double getWeatherConditionsMax() {
        return weatherConditionsMax;
    }

    public void setWeatherConditionsMax(double newWeatherConditionsMax) {
        System.out.println("[Weather Conditions Max = " + newWeatherConditionsMax + "]");
        Utility.setWeatherConditionsMax(newWeatherConditionsMax);
        weatherConditionsMax = newWeatherConditionsMax;
    }

    public double getWeatherConditionsMin() {
        return weatherConditionsMin;
    }

    public void setWeatherConditionsMin(double newWeatherConditionsMin) {
        System.out.println("[Weather Conditions Min = " + newWeatherConditionsMin + "]");
        Utility.setWeatherConditionsMin(newWeatherConditionsMin);
        weatherConditionsMin = newWeatherConditionsMin;
    }

    public Boolean isPoiFlag() {
        return this.poiFlag;
    }

    public Boolean getPoiFlag() {
        return this.poiFlag;
    }

    public void setPoiFlag(Boolean poiFlag) {
        if (poiFlag) {
            System.out.println("[STATIONS IN POINTS OF INTEREST]");
        }
        this.poiFlag = poiFlag;
    }

    public double getMonetaryIncentive() {
        return this.monetaryIncentive;
    }

    public void setMonetaryIncentive(double monetaryIncentive) {
        System.out.println("[Monetary Incentive = " + monetaryIncentive + "]");
        this.monetaryIncentive = monetaryIncentive;
    }

    public double getStaffTravelCost() {
        return this.staffTravelCost;
    }

    public void setStaffTravelCost(double staffTravelCost) {
        System.out.println("[Staff Travel Cost = " + staffTravelCost + "]");
        this.staffTravelCost = staffTravelCost;
    }

    public double getScooterPriceRate() {
        return this.scooterPriceRate;
    }

    public void setScooterPriceRate(double scooterPriceRate) {
        System.out.println("[Scooter Price Rate = " + scooterPriceRate + "]");
        this.scooterPriceRate = scooterPriceRate;
    }

    public int getNumberOfClients() {
        return this.numberOfClients;
    }

    public void setNumberOfClients(int numberOfClients) {
        System.out.println("[Number Of Clients = " + numberOfClients + "]");
        this.numberOfClients = numberOfClients;
    }

    public int getNumberOfScooters() {
        return this.numberOfScooters;
    }

    public void setNumberOfScooters(int numberOfScooters) {
        System.out.println("[Number Of Scooters = " + numberOfScooters + "]");
        this.numberOfScooters = numberOfScooters;
    }

    public int getNumberOfWorkers() {
        return this.numberOfWorkers;
    }

    public void setNumberOfWorkers(int numberOfWorkers) {
        System.out.println("[Number Of Workers = " + numberOfWorkers + "]");
        this.numberOfWorkers = numberOfWorkers;
    }

    public static int maxSpaceSize() {
        return spaceSize;
    }

    public int getSpaceSize() {
        return spaceSize;
    }

    public void setSpaceSize(int newSpaceSize) {
        spaceSize = newSpaceSize;
        System.out.println("space size is " + spaceSize);
    }

    public Boolean isVerboseFlag() {
        return this.verboseFlag;
    }

    public Boolean getVerboseFlag() {
        return this.verboseFlag;
    }

    public void setVerboseFlag(Boolean verboseFlag) {
        if (verboseFlag) {
            System.out.println("[VERBOSE MODE]");
        } else {
            System.out.println("[NON VERBOSE MODE]");
        }
        this.verboseFlag = verboseFlag;
        Utility.setVerbose(verboseFlag);
    }

    public Boolean isShowPlots() {
        return this.showPlots;
    }

    public Boolean getShowPlots() {
        return this.showPlots;
    }

    public void setShowPlots(Boolean showPlots) {
        if (showPlots) {
            System.out.println("[SHOW PLOTS ON]");
        } else {
            System.out.println("[SHOW PLOTS OFF]");
        }
        this.showPlots = showPlots;
    }

    public Boolean isShowDisplay() {
        return this.showDisplay;
    }

    public Boolean getShowDisplay() {
        return this.showDisplay;
    }

    public void setShowDisplay(Boolean showDisplay) {
        if (showDisplay) {
            System.out.println("[SHOW DISPLAY ON]");
        } else {
            System.out.println("[SHOW DISPLAY OFF]");
        }
        this.showDisplay = showDisplay;
    }
}