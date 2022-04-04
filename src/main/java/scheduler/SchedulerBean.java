package scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class to hold the current systems representation to eventually be converted to JSON
 * @author kevin
 *
 */
public class SchedulerBean {
	
	//Lists to dynamically add / delete elevator data
	private List<Integer> elevatorList;
	private List<Integer> elevatorAtFloorList;
	private List<Integer> floorRequestedList;
	private List<Integer> elevatorDirectionList;
	private List<Integer> elevatorStateList;
	private List<Integer> elevatorHardFaultList;
	
	private int numFloors;
	
	//Arrays to hold List data for JSON exporting
	private int[] elevatorArray;
	private int[] elevatorFloorArray;
	private int[] floorRequestedArray;
	private int[] elevatorDirectionArray;
	private int[] elevatorStateArray;

	public SchedulerBean() {
		elevatorList = new ArrayList<>();
		elevatorAtFloorList = new ArrayList<>();
		floorRequestedList = new ArrayList<>();
		elevatorDirectionList = new ArrayList<>();
		elevatorStateList = new ArrayList<>();
		elevatorHardFaultList = new ArrayList<>();
		}
	

	

	

	/**
	 * Add en elevatorId that has been hard faulted
	 * @param id
	 */
	public void addToHardFaults(Integer id) {
		this.elevatorHardFaultList.add(id);
	}
	
	/**
	 * Getter for hard fault list
	 * @return
	 */
	public List<Integer> getHardFaultList(){
		return this.elevatorHardFaultList;
	}
	
	/**
	 * Takes all lists can convert to array
	 */
	public void buildArraysForJsonOutput() {
		this.elevatorArray = this.elevatorList.stream()
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .toArray();
		
		this.elevatorFloorArray = this.elevatorAtFloorList.stream()
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .toArray();
		
		this.floorRequestedArray = this.floorRequestedList.stream()
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .toArray();
				
			
		this.elevatorDirectionArray = this.elevatorDirectionList.stream()
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .toArray();
		
		this.elevatorStateArray = this.elevatorStateList.stream()
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .toArray();
	}
	
	
	/**
	 * Finds the corresponding index that has the current elevatorId
	 * Since all the data about 1 elevator uses the same index in all lists/arrays
	 * @param elevatorId the elevatorId we are looking for
	 * @return
	 */
	public int findElevatorIndex(int elevatorId) {
		return this.elevatorList.indexOf(elevatorId);
	}
	
	
	/**
	 * Getter for Direction List
	 * @return
	 */
	public List<Integer> getDirectionList() {
		return this.elevatorDirectionList;
	}
	
	/**
	 * Getter for state list
	 * @return
	 */
	public List<Integer> getElevatorStateList(){
		return this.elevatorStateList;
	}
	
	/**
	 * Getter for ElevatorIdList
	 * @return
	 */
	public List<Integer> getElevatorList() {
		return elevatorList;
	}
	/**
	 * Getter for ElevatorCurrentFloorList
	 * @return
	 */
	public List<Integer> getElevatorFloorList() {
		return elevatorAtFloorList;
	}
	/**
	 * Getter for elevator request Floor List
	 * @return
	 */
	public List<Integer> getFloorRequestedList() {
		return floorRequestedList;
	}
	
	/**
	 * Setter for number of floors
	 * @param num
	 */
	public void setNumFloors(int num) {
		this.numFloors = num;
	}
	
	/**
	 * Getter for number of floors
	 * @return
	 */
	public int getNumFloors() {
		return this.numFloors;
	}
	/**
	 * Get elevatorIdArray
	 * @return
	 */
	public int[] getElevatorArray() {
		return this.elevatorArray;
	}
	
	/**
	 * Get elevatorCurrentFloorArray
	 * @return
	 */
	public int[] getElevatorFloorArray() {
		return this.elevatorFloorArray;
	}
	
	/**
	 * Get elevatorFloorRequestArray
	 * @return
	 */
	public int[] getRequestedFloorArray() {
		return this.floorRequestedArray;
	}
	
	
	/**
	 * Get Elevator Directions array
	 * @return
	 */
	public int[] getDirectionArray() {
		return this.elevatorDirectionArray;
	}
	
	/**
	 * Get elevator states array
	 * @return
	 */
	public int[] getStateArray() {
		return this.elevatorStateArray;
	}
	
}
