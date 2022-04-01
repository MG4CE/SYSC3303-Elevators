package scheduler;

import java.util.ArrayList;

import scheduler.ElevatorControl.ElevatorState;

public class SchedulerUtils {
	/**
	 * Process an elevator if its at a hard fault and reschedule all external requests to other elevators
	 * 
	 * @param e Elevator at fault
	 */
	protected static void hardFaultElevator(ArrayList<ElevatorControl> elevators, ElevatorControl e, int numFloors) {
		if(e.getState() != ElevatorState.TIMEOUT) {
			return;
		}
		
		Scheduler.LOGGER.error("Elevator " + e.getElevatorID() + ": has timed out, removing elevator");
		elevators.remove(e);
		if(elevators.size() >= 1) {
			Scheduler.LOGGER.info("Resceduling Elevator " + e.getElevatorID() + ": external button requests to other elevators");
			ArrayList<ElevatorRequest> pending = e.getAllExternalRequest();
			for (ElevatorRequest r : pending) {
				ElevatorSelection.assignBestElevator(r, elevators, numFloors);
			}
		}
		verifyElevatorTopRequests(elevators);
	}
	
	/**
	 * Verify if all the top requests for an elevator are the ones in progress and attempt to reschedule
	 * 
	 * Buggy as it can potentially send a dispatch requests when an elevator is inside the arrival state  
	 */
	protected static void verifyElevatorTopRequests(ArrayList<ElevatorControl> elevators) {
		for(ElevatorControl elevator : elevators) {
			synchronized(elevator){
				if(elevator.peekTopRequest() != null) {
					if(elevator.getNumDestinations() == 1 && elevator.getCurrentDestination() != elevator.peekTopRequest().getFloor()) {
						if(elevator.isTimeoutTimerOff()) {
							elevator.startTimeoutTimer();
						}
						elevator.setCurrentDestination(elevator.peekTopRequest().getFloor());
//						try {
//							sendSchedulerDispatchMessage(elevator.peekTopRequest().getFloor(), elevator.getPort(), elevator.peekTopRequest().getDirection(), elevator.peekTopRequest().getRequestID(), elevator.getElevatorID(), elevator.getAddress());
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
					}
				}
			}
		}
	}
}
