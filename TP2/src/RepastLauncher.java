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
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Object2DDisplay;
import uchicago.src.sim.space.Object2DGrid;

public class RepastLauncher extends Repast3Launcher {

    Boolean poiFlag = false;
    double monetaryIncentive = 0;
    double staffTravelCost = 0;
    double scooterPriceRate = 0;

    int numberOfClients = 0;
    int numberOfScooters = 0;
    int numberOfWorkers = 0;

    private Object2DGrid space;
    private Schedule schedule;
    private DisplaySurface dsurf;
    private int spaceSize;

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
        spaceSize = 100;
        agentList = new ArrayList<Agent>();
    }

    public void setup() {
        schedule = new Schedule();
        if (dsurf != null)
            dsurf.dispose();
        dsurf = new DisplaySurface(this, "Color Picking Display");
        registerDisplaySurface("Color Picking Display", dsurf);
    }

    @Override
    protected void launchJADE() {
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
                AgentController electricScooter = mainContainer.acceptNewAgent(name,
                        new ElectricScooterAgent(name, stationPositions.get(i % stationPositions.size())));
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

    public void begin() {
        super.begin();
		buildDisplay();
    }
    
    private void buildDisplay() {
		// space and display surface
		Object2DDisplay display = new Object2DDisplay(space);
		display.setObjectList(agentList);
		dsurf.addDisplayableProbeable(display, "Agents Space");
		dsurf.display();
	}


    public String[] getInitParam() {
        return new String[] { "poiFlag", "monetaryIncentive", "staffTravelCost", "scooterPriceRate", "numberOfClients",
                "numberOfScooters", "numberOfWorkers" };
        // return new String[] {};
    }

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
}