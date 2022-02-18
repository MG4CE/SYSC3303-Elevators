package components;

import elevators.Direction;

public class DirectionLamp {
	
	private Direction direction;
	private LightStatus lightStatus;
	
	public DirectionLamp() {
		this.direction = Direction.UP;
		this.lightStatus = LightStatus.OFF;
	}
	
	public Direction getDirection() {
		return direction;
	}
	
	public LightStatus getLightStatus() {
		return lightStatus;
	}
	
	public void turnOnLight() {
		this.lightStatus = LightStatus.ON;
	}
	
	public void turnOffLight() {
		this.lightStatus = LightStatus.OFF;
	}
	
	public void setDirectionUp() {
		this.direction = Direction.UP;
	}
	
	public void setDirectionDown() {
		this.direction = Direction.DOWN;
	}
}
