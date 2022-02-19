package elevators;

import java.util.ArrayList;

import commands.Command;
import commands.ElevatorArrivedMessage;
import commands.ElevatorMovingMessage;
import commands.InteriorElevatorBtnCommand;
import components.DirectionLamp;
import commands.ElevatorFloorSensorMessage;



public class Elevator implements Runnable {
	// FSM State Variables
	public enum State{IDLE, BOARDING, MOVING, ARRIVING};
	
	State currentState;
	
	final int NUM_FLOORS = 7;
	
	// Shared Command Variable
	Command latestCommand;
	Boolean readyForCommand;
	
	// Elevator instance variables
	Motor motor;
	Door elevatorDoor;
	DirectionLamp elevatorDirectionLamp;
	
	ArrayList<ElevatorButton> floorButtons;
	ArrayList<ArrivalSensor> sensors;
	ArrayList<ElevatorButtonLamp> floorButtonLamps;
	
	// Elevator fields
	Boolean running;
	int currentFloor;
	Direction currentDirection;
	int destinationFloor;

	
	public Elevator(){
		this.currentState = State.IDLE;
		currentFloor = 0;
		destinationFloor = 0;
		motor = new Motor();
		currentDirection = Direction.IDLE;
		latestCommand = null;
		readyForCommand = true;
		running = true;
		
		elevatorDirectionLamp = new DirectionLamp(currentDirection);
		
		elevatorDoor = new Door();
		elevatorDoor.closeDoor();
		
		//Add sensors
		sensors = new ArrayList<>();
		floorButtons = new ArrayList<>();
		floorButtonLamps = new ArrayList<>();
		
		for(int i =0; i<NUM_FLOORS; i++) {
			sensors.add(new ArrivalSensor(i));
			floorButtons.add(new ElevatorButton(i));
			floorButtonLamps.add(new ElevatorButtonLamp(i));
		}
		
	}
	
	public State getCurrentState() {
		return currentState;
	}
	
	public int getDestinationFloor() {
		return destinationFloor;
	}
	
	public void setDestinationFloor(int floor) {
		destinationFloor = floor;
	}	
	
	public Direction getDirection() {
		return currentDirection;
	}
	
	public void setDirection(Direction direction) {
		currentDirection = direction;
	}

	public void setCurrentFloor(int floor) {
		currentFloor = floor;
	}
	

	
	//TODO This is needed for replying to scheduler
	private void notifySchedulerOfState() {
		
		//This will need to tell the scheduler about the following events
		//1. After the elevator gets to a new floor
		//2. After an elevator goes from idle to moving
		//3. After the elevator goes from moving to arriving
		//4. After the elevator gets a new destination (Return old destination to be rescheduled)
		
		
	}
	
	


	public int getCurrentFloor() {
		return currentFloor;
	}

	// FSM Shit
	@Override
	public void run() {
		while(running) {
			updateFSM(getLatestCommand());
		}
	}
	
	public synchronized void elevatorPutCommand(Command command) {
		while(!readyForCommand) {
			try {
				wait();
			} catch (InterruptedException e) {
				System.out.print("something failed???");
			}
		}
		latestCommand = command;
		readyForCommand = false;
		notifyAll();
	}
	
	private synchronized Command getLatestCommand() {
		while(readyForCommand) {
			try {
				wait();
			} catch (InterruptedException e) {
				System.out.print("Fuck you");
			}
		}
		readyForCommand = true;
		notifyAll();
		return latestCommand;
	}
			
	/*
	 * FSM IMPLEMETNATION
	 */
	public synchronized void updateFSM(Command command) {
		switch (this.currentState){
		case IDLE :
			if(command instanceof InteriorElevatorBtnCommand) {
				InteriorElevatorBtnCommand c = (InteriorElevatorBtnCommand) command;
				if(this.currentFloor == c.getFloor()) {
					
					this.currentState = State.BOARDING;
					return;
				}else {
					currentState = State.MOVING;
					
					//Check direction
					if(this.currentFloor < c.getFloor()) {
						this.currentDirection = Direction.UP;
					}else if(this.currentFloor > c.getFloor()) {
						this.currentDirection = Direction.DOWN;
					}
					elevatorDirectionLamp = new DirectionLamp(currentDirection);
					elevatorDirectionLamp.turnOnLight();
					motor.move(currentDirection);
				}
			}
			break;
			
		case BOARDING:
			
			elevatorDoor.openDoor();
			
			elevatorDoor.closeDoor();
			
			//try {
			//	wait(30 * 1000); //TODO COME BACK
			//} catch (InterruptedException e) {
			//	if (currentState == State.BOARDING) 
			//		this.currentState = State.IDLE;
			//}
			// check if button on other floor was clicked
			if (command instanceof InteriorElevatorBtnCommand) {
				this.elevatorDirectionLamp.turnOffLight();
				this.currentState = State.IDLE;
			}
			break;
	
		
		case MOVING:
			// check if a higher priority stop was sent by the scheduler, back to MOVING
			if (command instanceof InteriorElevatorBtnCommand) {
				InteriorElevatorBtnCommand c = (InteriorElevatorBtnCommand) command;
				setDestinationFloor(c.getFloor()); // update latest destination
				currentState = State.MOVING;
			} 
			// Any time elevator passes a floor
			if(command instanceof ElevatorFloorSensorMessage) {
				ElevatorFloorSensorMessage c = (ElevatorFloorSensorMessage) command;
				setCurrentFloor(c.getFloor());
				if(currentDirection == Direction.UP && currentFloor == destinationFloor -1) {
					currentState = State.ARRIVING;
				}else if(currentDirection == Direction.DOWN && currentFloor == destinationFloor +1) {
					currentState = State.ARRIVING;
				}
			}
			break;
			
		case ARRIVING :
			this.motor.stopMotor();
			if(command instanceof ElevatorFloorSensorMessage) {
				currentState = State.BOARDING;
			}
			break;
		}		
	}	
}
