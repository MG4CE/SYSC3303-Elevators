package scheduler;

import commands.Command;
import elevators.Elevator;

public class Scheduler {

	public Scheduler() {
		// TODO Auto-generated constructor stub
	}

	public synchronized void ElevatorReply(Command cmd, Elevator e) {
		//Elevator has done an action and the reply
	}
	
	public synchronized void notifySchedulerOfChangeDestination(int OldFloor, Elevator e) {
		//Destination floor got overwritten, add previous destination floor back into queue
	}
	
}
