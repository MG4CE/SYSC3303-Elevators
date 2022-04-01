package scheduler;

import java.io.IOException;
import java.util.ArrayList;

import scheduler.ElevatorControl.ElevatorState;

public class SchedulerUtils {
	/**
	 * Process an elevator if its at a hard fault and reschedule all external requests to other elevators
	 * 
	 * @param e Elevator at fault
	 */
	protected static void hardFaultElevator(Scheduler s, ElevatorControl e, int numFloors) {
		if(e.getState() != ElevatorState.TIMEOUT) {
			return;
		}
		
		Scheduler.LOGGER.error("Elevator " + e.getElevatorID() + ": has timed out, removing elevator");
		s.elevators.remove(e);
		if(s.elevators.size() >= 1) {
			Scheduler.LOGGER.info("Resceduling Elevator " + e.getElevatorID() + ": external button requests to other elevators");
			ArrayList<ElevatorRequest> pending = e.getAllExternalRequest();
			for (ElevatorRequest r : pending) {
				ElevatorSelection.assignBestElevator(r, s.elevators, numFloors);
			}
		}
		verifyElevatorTopRequests(s);
	}
	
	/**
	 * Verify if all the top requests for an elevator are the ones in progress and attempt to reschedule
	 */
	protected static void verifyElevatorTopRequests(Scheduler s) {
		for(ElevatorControl elevator : s.elevators) {
			synchronized(elevator){
				if(elevator.peekTopRequest() != null) {
					if(elevator.getCurrentDestination() != elevator.peekTopRequest().getFloor() && elevator.isTopRequestSchedulable()) {
						if(elevator.isTimeoutTimerOff()) {
							elevator.startTimeoutTimer();
						}
						elevator.setCurrentDestination(elevator.peekTopRequest().getFloor());
						try {
							SchedulerMessages.sendSchedulerDispatchMessage(s, elevator.peekTopRequest().getFloor(), elevator.getPort(), elevator.peekTopRequest().getDirection(), elevator.peekTopRequest().getRequestID(), elevator.getElevatorID(), elevator.getAddress());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
}
