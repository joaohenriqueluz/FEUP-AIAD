import sajas.core.Agent;
import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.space.Multi2DGrid;
import java.awt.Color;

public class StationDrawableAgent extends Agent implements Drawable {

    Position position;
    Multi2DGrid space;
    Color color;
    Boolean isStation;

    public StationDrawableAgent(Position position, Multi2DGrid space, Color color, Boolean isItstation) {
        this.position = position;
        this.space = space;
        this.color = color;
        this.isStation = isItstation;
    }

    @Override
    public void draw(SimGraphics g) {
        if (isStation) {
            int number = this.space.getObjectsAt(this.getX(), this.getY()).size();
            g.setXScale(2 * number);
            g.setYScale(2 * number);
        }
        g.drawHollowFastOval(this.color);
        g.setXScale(2);
        g.setYScale(2);
    }

    @Override
    public int getX() {
        return this.position.getX();
    }

    @Override
    public int getY() {
        return this.position.getY();
    }

    public Boolean isIsStation() {
        return this.isStation;
    }

    public Boolean getIsStation() {
        return this.isStation;
    }

    public void setIsStation(Boolean isStation) {
        this.isStation = isStation;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

}
