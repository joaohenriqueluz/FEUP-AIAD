import sajas.core.Agent;
import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.space.Multi2DGrid;
import java.awt.Color;


public class StationDrawableAgent extends Agent implements Drawable {

    Position position;
    Multi2DGrid space;
    Color color;


    public StationDrawableAgent(Position position, Multi2DGrid space) {
        this.position = position;
        this.space = space;
        this.color = new Color(255,255, 255);
    }

    public StationDrawableAgent(Position position, Multi2DGrid space, Color color) {
        this.position = position;
        this.space = space;
        this.color = color;
    }

    @Override
    public void draw(SimGraphics g) {
        g.drawOval(this.color); 
        g.setXScale(3);
        g.setYScale(3);
    }

    @Override
    public int getX() {
       return this.position.getX();
    }

    @Override
    public int getY() {
       return this.position.getY();
    }

}
