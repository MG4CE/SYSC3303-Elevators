package elevators;

public class ElevatorButton {
	
	private int floor;
	
	public ElevatorButton(int floor) {
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
