package elevators;

public class FloorButton {
	private int floor;
	private Direction direction;
	
	public FloorButton(int floor, Direction direction) {
		this.direction = direction;
		this.floor = floor;
	}
	
	public int getFloor() {
		return floor;
	}
	
	/* this should create and return a command that needs to be send to the scheduler
	* public Command pressButton() {
	* 	
	* }
	*/
}