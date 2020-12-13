import sajas.core.Agent;
import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.space.Multi2DGrid;
import java.awt.Color;


public class StationDrawableAgent extends Agent implements Drawable {

    Position position;
    Multi2DGrid space;
    Color color;

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

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
        int number = this.space.getObjectsAt(this.getX(), this.getY()).size();
        g.setXScale(2*number);
        g.setYScale(2*number);
        g.drawHollowFastOval(this.color);
        g.setXScale(2*1);
        g.setYScale(2*1);
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
