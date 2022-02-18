package elevators;

import java.util.concurrent.TimeoutException;

import commands.Command;
import commands.ElevatorArrivedMessage;
import commands.ElevatorMovingMessage;
import commands.ElevatorRequestCommand;
import scheduler.Scheduler;
import stateMachines.ElevatorFSM;
import stateMachines.ElevatorFSM.State;

public class Elevator implements Runnable {
	// instance variables
	Motor motor;
	ElevatorFSM fsm;
	
	// state holding variables
	int currentFloor;
	Direction currentDirection;
	int destinationFloor;
	Scheduler scheduler;
	
	Command command;
	
	
	
	public Elevator(Scheduler scheduler){
		fsm = new ElevatorFSM();
		currentFloor = 0;
		destinationFloor = 0;
		motor = new Motor();
		currentDirection = Direction.IDLE;
		command = null;
		this.scheduler = scheduler;
	}
	
	public int getCurrentFloor() {
		return this.currentFloor;
	}
	
	//TODO REMOVE AFTER WORKING, HERE FOR TESTS!
	public void setCurrentFloor(int floor) {
		this.currentFloor = floor;
	}
	
	public int getDestinationFloor() {
		return this.destinationFloor;
	}
	
	public void setDestinationFloor(int floor) {
		this.destinationFloor = floor;
	}	
	
	public Direction getDirection() {
		return this.currentDirection;
	}
	
	public void setDirection(Direction direction) {
		this.currentDirection = direction;
	}
	
	public ElevatorFSM.State getElevatorState(){
		return fsm.getState();
	}
	
	public synchronized void setCurrentCommand(Command cmd) {
		this.command = cmd;
	}
	
	
	
	
	
	/*
	 * Wait for the latest command, add synchronize wait
	 */
	public synchronized void onCommand(Command cmd) {
		ElevatorFSM.State prevState = fsm.getState();
		ElevatorFSM.State s = fsm.nextState(cmd, this);
		switch (s){
		case INIT :
			//This case shouldnt really happen
			break;
		case IDLE :
			//We get a new command from the shceduler or we wait
			//Blank command to wait for another one
			this.command = null;
			
			break;
		case BOARDING:
			doorOpen();
			try {
				//Let people get on
				Thread.sleep(30);
			}catch(Exception e) {}
			doorClose();
			
			try {
				wait(300);
			}catch (InterruptedException e) {
				//No button press therefore go back to idle and wait for another command
				fsm.nextState(null, this);
				this.command = null;
			}
			
			//wait for 30s
			break;
		case MOVING:
			if(prevState.equals(State.IDLE)) {
				notifySchedulerOfMessage(new ElevatorMovingMessage(getCurrentFloor(), 123));
			}
			this.motor.move(currentDirection);
			
			
			break;
		case ARRIVING :
			this.motor.stopMotor();
			notifySchedulerOfMessage(new ElevatorArrivedMessage(getCurrentFloor(), 123));
			
			
			break;
		default:
			break;
		}
	}
	
	public void notifySchedulerOfMessage(Command cmd) {
		this.scheduler.ElevatorReply(cmd, this);
	}
	
	public void notifySchedulerOfChangeDestination() {
		
	}
	
	private void doorOpen() {
		System.out.println("Elevator Doors openning");
	}
	private void doorClose() {
		System.out.println("Elevator Doors Closing");
	}
	
	
	@Override
	public void run() {
		while(true) {
			while(this.command != null) {
				try {
					wait();
				}catch(Exception e) {}
			}
			onCommand(this.command);
		}
		
	}
	
	
}
