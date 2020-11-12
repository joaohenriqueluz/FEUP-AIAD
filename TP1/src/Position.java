import java.util.Random;
import java.util.Objects;

public class Position {

    private int x;
    private int y;

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

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position() {
        Random rand = new Random();
        this.x = rand.nextInt(1000);
        this.y = rand.nextInt(1000);
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

}
