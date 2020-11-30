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
import uchicago.src.sim.engine.BasicAction;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Object2DDisplay;
import uchicago.src.sim.space.Object2DGrid;
import uchicago.src.sim.util.SimUtilities;

public class RepastLauncher extends Repast3Launcher {

    Boolean poiFlag = false;
    double monetaryIncentive = 0;
    double staffTravelCost = 0;
    double scooterPriceRate = 0;

    int numberOfClients = 1;
    int numberOfScooters = 1;
    int numberOfWorkers = 1;

    private Object2DGrid space;
    private Schedule schedule;
    private DisplaySurface dsurf;
    public static int spaceSize = 150;

    private ArrayList<Agent> agentList;

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
        space = new Object2DGrid(spaceSize, spaceSize);

        try {
            CompanyAgent companyAgent = new CompanyAgent(10, "company_0", poiFlag, monetaryIncentive, staffTravelCost,
                    scooterPriceRate);
            AgentController company = mainContainer.acceptNewAgent("company", companyAgent);
            company.start();

            ArrayList<Position> stationPositions = companyAgent.getChargingStationPositions();
            for (int i = 0; i < numberOfScooters; i++) {
                String name = "electricScooter_" + i;
                ElectricScooterAgent electricScooterAgent = new ElectricScooterAgent(name,
                        stationPositions.get(i % stationPositions.size()), space);
                AgentController electricScooter = mainContainer.acceptNewAgent(name, electricScooterAgent);
                space.putObjectAt(electricScooterAgent.getPosition().getX(), electricScooterAgent.getPosition().getY(),
                        this);
                agentList.add(electricScooterAgent);
                electricScooter.start();
            }

            for (int i = 0; i < numberOfClients; i++) {
                String name = "client_" + i;
                ClientAgent clientAgent = new ClientAgent(name, space);
                AgentController client = mainContainer.acceptNewAgent(name, clientAgent);
                space.putObjectAt(clientAgent.getPosition().getX(), clientAgent.getPosition().getY(), this);
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
                space.putObjectAt(workerAgent.getPosition().getX(), workerAgent.getPosition().getY(), this);
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

    private void buildDisplay() {
        System.out.println("BuildDisplay");
        // space and display surface
        Object2DDisplay display = new Object2DDisplay(space);
        display.setObjectList(agentList);
        dsurf.addDisplayableProbeable(display, "Agents Space");
        dsurf.display();
    }

	private void buildSchedule() {
        System.out.println("Aaaaann schedule!");

		schedule.scheduleActionBeginning(0, new MainAction());
		schedule.scheduleActionAtInterval(1, dsurf, "updateDisplay", Schedule.LAST);
    }

    class MainAction extends BasicAction {
        public void execute() {
            System.out.println("Aaaaann ACTIoN!");
            // shuffle agents
            SimUtilities.shuffle(agentList);

            // iterate through all agents
            for (int i = 0; i < agentList.size(); i++) {
                ClientAgent client = (ClientAgent) agentList.get(i);
                if (!client.isBusy()) {
                    client.addBehaviour(new RequestScooterInitiator(client, new ACLMessage(ACLMessage.REQUEST)));
                }
            }
        }

    }

    @Override
    public String[] getInitParam() {
        return new String[] { "spaceSize", "poiFlag", "monetaryIncentive", "staffTravelCost", "scooterPriceRate",
                "numberOfClients", "numberOfScooters", "numberOfWorkers" };
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
}