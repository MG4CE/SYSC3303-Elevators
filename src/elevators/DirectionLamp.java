package elevators;

public class DirectionLamp {	
	private DirectionType direction;
	private ElevatorStatus status;
	
	public DirectionLamp() {
		this.direction = DirectionType.UP;
		this.status = ElevatorStatus.IDLE;
	}
	
	public DirectionType getDirection() {
		return direction;
	}
	
	public ElevatorStatus getStatus() {
		return status;
	}
	
	public void setDirection(DirectionType direction) {
		this.direction = direction;
	}
	
	public void setStatus(ElevatorStatus status) {
		this.status = status;
	}
}
