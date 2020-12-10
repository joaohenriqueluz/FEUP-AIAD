import sajas.core.AID;
import jade.lang.acl.ACLMessage;
import sajas.proto.AchieveREInitiator;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

class RequestPickUpInitiator extends AchieveREInitiator {
    ElectricScooterAgent scooter;

    public RequestPickUpInitiator(ElectricScooterAgent scooter, ACLMessage msg) {
        super(scooter, msg);
        this.scooter = scooter;
        Position start = this.scooter.getTripStartPosition();
        Position end = this.scooter.getPosition();
        double distance = Utility.getEuclideanDistance(start, end);
        msg.setContent("PICK-UP=>" + this.scooter.getPosition().toString() + "--" + distance + "--"
                + this.scooter.getAID().getName());
        AID companyAgent = this.scooter.getYellowPagesService().getAgentList("company")[0];
        if (companyAgent != null) {
            msg.addReceiver(companyAgent);
        }
    }

    protected void handleAgree(ACLMessage agree) {
        Utility.log(this.scooter, agree);
    }

    protected void handleRefuse(ACLMessage refuse) {
        Utility.log(this.scooter, refuse);
        this.scooter.setBusy(false);
    }

    protected void handleInform(ACLMessage inform) {
        Utility.log(this.scooter, inform);
    }

    protected void handleFailure(ACLMessage failure) {
        Utility.log(this.scooter, failure);
        scooter.setBusy(false);
    }
}