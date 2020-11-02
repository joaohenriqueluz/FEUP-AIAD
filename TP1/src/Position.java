import java.util.Random;

public class Position {

	private Integer x;
	private Integer y;

	public Integer getX() {
		return this.x;
	}

	public void setX(Integer x) {
		this.x = x;
	}

	public Integer getY() {
		return this.y;
	}

	public void setY(Integer y) {
		this.y = y;
	}

	public Position(Integer x, Integer y) {
		this.x = x;
		this.y = y;
	}

	public Position() {
		Random rand = new Random();
		this.x = rand.nextInt(20);
		this.y = rand.nextInt(20);
	}

	public String toString(){
		return this.x + "," + this.y;
	}

}
