import java.util.Objects;
import java.util.Random;
import java.awt.Color;
import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;
public class Position implements Drawable {

    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position() {
        Random rand = new Random();
        this.x = rand.nextInt(RepastLauncher.maxSpaceSize() - 1);
        this.y = rand.nextInt(RepastLauncher.maxSpaceSize() - 1);
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String toString() {
        return this.x + "," + this.y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true; // are the references equal
        if (o == null)
            return false; // is the other object null
        if (getClass() != o.getClass())
            return false; // both objects the same class
        Position p = (Position) o; // cast the other object
        return x == p.getX() && y == p.getY(); // actual comparison
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public void draw(SimGraphics g) {
        g.drawFastCircle(new Color(255,255,0));
    }

}
