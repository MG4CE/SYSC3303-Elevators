package elevators;

import java.util.ArrayList;

import commands.Command;
import commands.ElevatorArrivedMessage;
import commands.ElevatorDispatchCommand;
import commands.ElevatorMovingMessage;
import commands.InteriorElevatorBtnCommand;
import components.DirectionLamp;
import scheduler.Scheduler;
import commands.ElevatorFloorSensorMessage;


/**
 * 
 * @author kevin
 *
 */
public class Elevator implements Runnable {
	// FSM State Variables
	public enum State{IDLE, BOARDING, MOVING, ARRIVING};
	
	State currentState;
	
	
	final int NUM_FLOORS = 7;
	final int ELEVATOR_ID;
	
	// Shared Command Variable
	Command latestCommand;
	Boolean readyForCommand;
	Scheduler schedulator;
	
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

	/**
	 * Elevator Initializer
	 * @param s the reference to shceduler
	 * @param elevatorId the id of the elevator
	 */
	public Elevator(Scheduler s, int elevatorId){
		ELEVATOR_ID = elevatorId;
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
		
		//Add sensors lists
		sensors = new ArrayList<>();
		floorButtons = new ArrayList<>();
		floorButtonLamps = new ArrayList<>();
		
		//Initialize all the sensors in the lists
		for(int i =0; i<NUM_FLOORS; i++) {
			sensors.add(new ArrivalSensor(i));
			floorButtons.add(new ElevatorButton(i));
			floorButtonLamps.add(new ElevatorButtonLamp(i));
		}
		
		//Scheduler to respond to
		schedulator = s;
		
	}
	
	/**
	 * Getter for current state
	 * @return State currentState
	 */
	public State getCurrentState() {
		return currentState;
	}
	
	/**
	 * Getters for current destination floor
	 * @return int
	 */
	public int getDestinationFloor() {
		return destinationFloor;
	}
	/**
	 * Setter for current Destination floor
	 * @param floor int
	 */
	public void setDestinationFloor(int floor) {
		destinationFloor = floor;
	}	
	
	/**
	 * Getter for the elevators current moving direction
	 * @return Direction
	 */
	public Direction getDirection() {
		return currentDirection;
	}
	
	/**
	 * Setter for the elevators current Direction
	 * @param direction
	 */
	public void setDirection(Direction direction) {
		currentDirection = direction;
	}

	/**
	 * Setter for currentFloor 
	 * @param floor int
	 */
	public synchronized void setCurrentFloor(int floor) {
		currentFloor = floor;
	}
	
	/**
	 * Getter for the elevators current Floor
	 * @return int current floor
	 */
	public synchronized int getCurrentFloor() {
		return currentFloor;
	}
	
	/**
	 * This will be called from FloorSubSystem to simulate an inside button press
	 * @param destinationFloor int
	 */
	public void buttonPush(int destinationFloor) {
		this.schedulator.schedulerPutCommand(new InteriorElevatorBtnCommand(destinationFloor, ELEVATOR_ID));
	}
	
	/**
	 * Ferry response commands to the scheduler
	 * Can be a ElevatorMovingMessage or ElevatorArrivedMessage
	 * 
	 * @param response COmmand
	 */
	private void notifySchedulerOfState(Command response) {
		this.schedulator.schedulerPutCommand(response);
	}
	
	
	

	// FSM Shit
	@Override
	public void run() {
		while(running) {
			updateFSM(getLatestCommand());
		}
	}
	
	/**
	 * Put commands to the elevator to handle
	 * Comes from
	 * Shceduler - ElevatorDispatchCommand
	 * Motor - ElevatorFloorSensorMessage
	 * @param command
	 */
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
	
	/**
	 * This elevator consuming the new command or wait if not set
	 * @return
	 */
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
			if(command instanceof ElevatorDispatchCommand) {
				ElevatorDispatchCommand c = (ElevatorDispatchCommand) command;
				if(this.currentFloor == c.getDestFloor()) {
					this.currentState = State.BOARDING;
					return;
				}else {
					currentState = State.MOVING;
					
					//Check direction
					if(this.currentFloor < c.getDestFloor()) {
						this.currentDirection = Direction.UP;
					}else if(this.currentFloor > c.getDestFloor()) {
						this.currentDirection = Direction.DOWN;
					}
					elevatorDirectionLamp = new DirectionLamp(currentDirection);
					elevatorDirectionLamp.turnOnLight();
					motor.move(currentDirection);
					
					
					notifySchedulerOfState(new ElevatorMovingMessage(ELEVATOR_ID, c.getDestFloor(), currentDirection));
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
			if (command instanceof ElevatorDispatchCommand) {
				ElevatorDispatchCommand c = (ElevatorDispatchCommand) command;
				setDestinationFloor(c.getDestFloor()); // update latest destination
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
				notifySchedulerOfState(new ElevatorArrivedMessage(ELEVATOR_ID, currentFloor));
			}
			break;
		}		
	}	
}
