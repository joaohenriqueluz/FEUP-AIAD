import java.lang.reflect.Array;
import jdk.jshell.execution.Util;
import java.util.ArrayList;

import sajas.sim.repast3.Repast3Launcher;
import sajas.core.Agent;
import sajas.core.Runtime;
import sajas.wrapper.AgentController;
import sajas.wrapper.ContainerController;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.StaleProxyException;
import jade.lang.acl.ACLMessage;
import uchicago.src.sim.analysis.OpenSequenceGraph;
import uchicago.src.sim.analysis.Sequence;
import uchicago.src.sim.engine.BasicAction;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Object2DDisplay;
import uchicago.src.sim.space.BagCell;
import uchicago.src.sim.space.Multi2DGrid;
import uchicago.src.sim.space.Multi2DGrid;
import uchicago.src.sim.util.SimUtilities;

public class RepastLauncher extends Repast3Launcher {

    Boolean poiFlag = false;
    Boolean verboseFlag = false;
    double monetaryIncentive = 0;
    double staffTravelCost = 0;
    double scooterPriceRate = 0;

    int numberOfClients = 1;
    int numberOfScooters = 1;
    int numberOfWorkers = 1;

    public static Multi2DGrid space;
    private Schedule schedule;
    private DisplaySurface dsurf;
    public static int spaceSize = 150;

    private ArrayList<Agent> agentList;
    private CompanyAgent companyAgent;

    public static void main(String[] args) {
        boolean runMode = false; // BATCH_MODE or !BATCH_MODE
        // create a simulation
        SimInit init = new SimInit();
        // create a model
        RepastLauncher model = new RepastLauncher();
        // load model into simulation:
        init.loadModel(model, null, runMode);
    }

    public RepastLauncher() {
        super();
        agentList = new ArrayList<Agent>();
    }

    @Override
    public void setup() {
        super.setup();
        System.out.println("setup");
        schedule = new Schedule();
        if (dsurf != null)
            dsurf.dispose();
        dsurf = new DisplaySurface(this, "Color Picking Display");
        registerDisplaySurface("Color Picking Display", dsurf);
    }

    @Override
    public void launchJADE() {
        System.out.println("launchJADE");
    }

    protected void launchAgents() {
        System.out.println("Launching JADE");
        Runtime rt = Runtime.instance();
        Profile p1 = new ProfileImpl();
        ContainerController mainContainer = rt.createMainContainer(p1);
        space = new Multi2DGrid(spaceSize, spaceSize, false);

        try {
            companyAgent = new CompanyAgent(10, "company_0", poiFlag, monetaryIncentive, staffTravelCost,
                    scooterPriceRate, space);
            AgentController company = mainContainer.acceptNewAgent("company", companyAgent);
            company.start();

            ArrayList<Position> stationPositions = companyAgent.getChargingStationPositions();
            for (int i = 0; i < numberOfScooters; i++) {
                String name = "electricScooter_" + i;
                ElectricScooterAgent electricScooterAgent = new ElectricScooterAgent(name,
                        stationPositions.get(i % stationPositions.size()), space);
                AgentController electricScooter = mainContainer.acceptNewAgent(name, electricScooterAgent);
                BagCell cell = new BagCell();
                cell.add(electricScooterAgent);
                space.putObjectAt(electricScooterAgent.getPosition().getX(), electricScooterAgent.getPosition().getY(),
                        cell);
                agentList.add(electricScooterAgent);
                electricScooter.start();
            }

            for (int i = 0; i < numberOfClients; i++) {
                String name = "client_" + i;
                ClientAgent clientAgent = new ClientAgent(name, space);
                AgentController client = mainContainer.acceptNewAgent(name, clientAgent);
                BagCell cell = new BagCell();
                cell.add(clientAgent);
                space.putObjectAt(clientAgent.getPosition().getX(), clientAgent.getPosition().getY(), cell);
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
                BagCell cell = new BagCell();
                cell.add(workerAgent);
                space.putObjectAt(workerAgent.getPosition().getX(), workerAgent.getPosition().getY(), cell);
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
        System.out.println("begin");
        launchAgents();
        buildDisplay();
        buildSchedule();
    }

    private OpenSequenceGraph plotIncome;
    private OpenSequenceGraph plotSuccessuful;

    private void buildDisplay() {
        System.out.println("BuildDisplay");
        // space and display surface
        Object2DDisplay display = new Object2DDisplay(space);
        display.setObjectList(agentList);
        dsurf.addDisplayableProbeable(display, "Agents Space");
        dsurf.display();

        // graph
        if (plotSuccessuful != null)
            plotSuccessuful.dispose();
        plotSuccessuful = new OpenSequenceGraph("Scooter service performance", this);
        plotSuccessuful.setAxisTitles("time", "% successful trips");
        plotSuccessuful.setYRange(0, 100);

        plotSuccessuful.addSequence("Consumers", new Sequence() {
            public double getSValue() {
                return 100 * ((double) companyAgent.getNumberOfSuccessfulTrips()) / companyAgent.getNumberOfTrips();
            }
        });
        plotSuccessuful.display();

        if (plotIncome != null)
            plotIncome.dispose();
        plotIncome = new OpenSequenceGraph("Scooter service performance", this);
        plotIncome.setAxisTitles("time", "Monetary Units");
        plotIncome.addSequence("Average Income", new Sequence() {
            public double getSValue() {
                return ((double) companyAgent.getNetIncome()) / companyAgent.getNumberOfTrips();
            }
        });

        plotIncome.addSequence("Average Trip Operation Cost", new Sequence() {
            public double getSValue() {
                return ((double) companyAgent.getOperationCosts()) / companyAgent.getNumberOfTrips();
            }
        });

        plotIncome.addSequence("Average Profit", new Sequence() {
            public double getSValue() {
                return (((double) companyAgent.getNetIncome()) - companyAgent.getOperationCosts())
                        / companyAgent.getNumberOfTrips();
            }
        });

        plotIncome.display();
    }

    private void buildSchedule() {
        System.out.println("Aaaaann schedule!");

        // schedule.scheduleActionBeginning(0, new MainAction());
        getSchedule().scheduleActionAtInterval(1, dsurf, "updateDisplay", Schedule.LAST);
        getSchedule().scheduleActionAtInterval(100, plotSuccessuful, "step", Schedule.LAST);
        getSchedule().scheduleActionAtInterval(100, plotIncome, "step", Schedule.LAST);
    }

    @Override
    public String[] getInitParam() {
        return new String[] { "spaceSize", "poiFlag", "verboseFlag", "monetaryIncentive", "staffTravelCost",
                "scooterPriceRate", "numberOfClients", "numberOfScooters", "numberOfWorkers" };
        // return new String[] {};
    }

    @Override
    public String getName() {
        return "Electric Scooter Simulation";
    }

    public Boolean isPoiFlag() {
        return this.poiFlag;
    }

    public Boolean getPoiFlag() {
        return this.poiFlag;
    }

    public void setPoiFlag(Boolean poiFlag) {
        this.poiFlag = poiFlag;
    }

    public double getMonetaryIncentive() {
        return this.monetaryIncentive;
    }

    public void setMonetaryIncentive(double monetaryIncentive) {
        this.monetaryIncentive = monetaryIncentive;
    }

    public double getStaffTravelCost() {
        return this.staffTravelCost;
    }

    public void setStaffTravelCost(double staffTravelCost) {
        this.staffTravelCost = staffTravelCost;
    }

    public double getScooterPriceRate() {
        return this.scooterPriceRate;
    }

    public void setScooterPriceRate(double scooterPriceRate) {
        this.scooterPriceRate = scooterPriceRate;
    }

    public int getNumberOfClients() {
        return this.numberOfClients;
    }

    public void setNumberOfClients(int numberOfClients) {
        this.numberOfClients = numberOfClients;
    }

    public int getNumberOfScooters() {
        return this.numberOfScooters;
    }

    public void setNumberOfScooters(int numberOfScooters) {
        this.numberOfScooters = numberOfScooters;
    }

    public int getNumberOfWorkers() {
        return this.numberOfWorkers;
    }

    public void setNumberOfWorkers(int numberOfWorkers) {
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
    }

    public Boolean isVerboseFlag() {
        return this.verboseFlag;
    }

    public Boolean getVerboseFlag() {
        return this.verboseFlag;
    }

    public void setVerboseFlag(Boolean verboseFlag) {
        this.verboseFlag = verboseFlag;
        Utility.setVerbose(verboseFlag);
    }
}