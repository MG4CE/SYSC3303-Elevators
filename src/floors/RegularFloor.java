package floors;

import commands.ExternalFloorBtnCommand;
import components.DirectionLamp;
import elevators.Direction;

public class RegularFloor extends Floor{
	
	private DirectionLamp upDirectionLamp;
	private DirectionLamp downDirectionLamp;
	private FloorButton upButton;
	private FloorButton downButton;
	private FloorButtonLamp upButtonLamp;
	private FloorButtonLamp downButtonLamp;
	
	public RegularFloor(int floor) {
		super(floor);
		this.upDirectionLamp = new DirectionLamp(Direction.UP);
		this.downDirectionLamp = new DirectionLamp(Direction.DOWN);
		this.upButton = new FloorButton(floor, Direction.UP);
		this.downButton = new FloorButton(floor, Direction.DOWN);
		this.upButtonLamp = new FloorButtonLamp(floor);
		this.downButtonLamp = new FloorButtonLamp(floor);
	}
	
	public void turnOnDirectionLamp(Direction direction) {
		if(direction == Direction.UP) {
			upDirectionLamp.turnOnLight();
		} else if (direction == Direction.DOWN) {
			downDirectionLamp.turnOnLight();
		}
	}
	
	public void turnOffDirectionLamp(Direction direction) {
		if(direction == Direction.UP) {
			upDirectionLamp.turnOnLight();
		} else if (direction == Direction.DOWN) {
			downDirectionLamp.turnOnLight();
		}
	}
	
	public void turnOnButtonLamp(Direction direction) {
		if(direction == Direction.UP) {
			upButtonLamp.turnOn();
		} else if (direction == Direction.DOWN) {
			downButtonLamp.turnOn();
		}
	}
	
	public void turnOffButtonLamp(Direction direction) {
		if(direction == Direction.UP) {
			upButtonLamp.turnOff();
		} else if (direction == Direction.DOWN) {
			downButtonLamp.turnOff();
		}
	}
	
	public ExternalFloorBtnCommand pushButton(Direction direction) {
		if(direction == Direction.UP) {
			return upButton.pushButton();
		} else if (direction == Direction.DOWN) {
			return downButton.pushButton();
		}
		return null;
	}
}
