package scheduler;


import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import org.json.simple.JSONObject;

/**
 * Class that will run as extra thread. It holds a tcp socket that can be used as an API for Front end
 * @author kevin
 *
 */
public class SchedulerTCPServer  implements Runnable {
	private int socketNum = 107;
	private Socket webpage;
	private ServerSocket server;
	private boolean running = true;
	private Scheduler scheduler;
	private SchedulerBean bean;
	int numberOfElevators = 0;

	public SchedulerTCPServer(Scheduler scheduler) throws IOException {
		try {
			//Add the scheduler here
			this.scheduler = scheduler;
			//Start the TCP server
			server = new ServerSocket(socketNum);
			//Create the FrontEnd data bean
			this.bean = new SchedulerBean();
			//Add the tcp server to scheduler so it can comm the hard faults
			this.scheduler.addServerToScheduler(this);
		} catch (IOException e) {
			
			e.printStackTrace();
			server.close();
			running = false;
		}		
		
	}
	
	@Override
	public void run() {
		while(running) {
			try {
				runServer();
			} catch (IOException e) {
				try {
					server.close();
					running = false;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Method used from SchedulerUtils to add to hardFaulted list
	 * this represents what elevators have Hard Faulted
	 * @param elevatorId
	 */
	protected void addToHardFaults(Integer elevatorId) {
		this.bean.addToHardFaults(elevatorId);
	}
	
	/**
	 * This method would be used to update all the information in the Bean Object
	 */
	protected void updateSchedulerInfo() {
		for (ElevatorControl elevator : this.scheduler.getElevatorControl()) {
			int index;
			//Found old elevator update its values
			if((index = bean.findElevatorIndex(elevator.getElevatorID())) != -1) {
				bean.getElevatorFloorList().set(index, elevator.getCurrentFloor());
				bean.getFloorRequestedList().set(index, elevator.getCurrentDestination());
				bean.setNumFloors(scheduler.numFloors);
				bean.getElevatorStateList().set(index, translateState(elevator));
				bean.getDirectionList().set(index, translateDirection(elevator.getCurrentDestination(), elevator.getCurrentFloor()));
			}
			else {
				//New elevator add its defaults
				bean.getElevatorList().add(elevator.getElevatorID());
				bean.getElevatorFloorList().add(elevator.getCurrentFloor()+1);
				bean.getFloorRequestedList().add(elevator.getCurrentDestination()+1);
				bean.setNumFloors(scheduler.numFloors);
				bean.getElevatorStateList().add(0);
				bean.getDirectionList().add(0);
				this.numberOfElevators++;
			}
			//Check if a hard faulted elevator and set its state to 9
			checkForHardFaults(this.scheduler.getElevatorControl());
		}
		bean.buildArraysForJsonOutput();
	}
	
	/**
	 * Method used to check if any elevators have hard faulted
	 * @param elevatorControl
	 */
	private void checkForHardFaults(ArrayList<ElevatorControl> elevatorControl) {
		int index = -1;
		
		for(Integer elevatorIdThatHF: this.bean.getHardFaultList()) {
			if((index = this.bean.findElevatorIndex(elevatorIdThatHF))!=-1) {
				this.bean.getElevatorStateList().set(index, 9);
			}
		}
	}

	/**
	 * Since ElevatorControl doesnt wait for the elevator to actually move
	 * I have to translate that if the elevator Control is not on the destination floor, its moving or faulted
	 * @param ev
	 * @return
	 */
	public int translateState(ElevatorControl ev) {
		if(ev.getState().equals(ElevatorControl.ElevatorState.STOPPED) & ev.getCurrentFloor() != ev.getCurrentDestination()) {
			return 1;
		}else {
			
		}
		return ev.getState().ordinal();
	}
	
	/**
	 * Translate direction
	 * up direction as 2
	 * down direction as 1
	 * idle as 0
	 * 
	 * @param destFloor
	 * @param currFloor
	 * @return
	 */
	public int translateDirection(int destFloor, int currFloor) {
		if(destFloor > currFloor) {
			return 2;
		}	
		else if(destFloor < currFloor) {
			return 1;
		}
		else
			return 0;
	}
	
	/**
	 * Rung the server
	 * @throws IOException
	 */
	public void runServer() throws IOException {
			//Method to wait for ajax call
		    listenForCall();
		    //Update the front end
			updateFrontEnd();
	}
	
	/**
	 * Waits for incoming tcp call
	 * @throws IOException
	 */
	private void listenForCall() throws IOException {
		webpage = server.accept();
	}
	
	/**
	 * Update the front end by translating all system data into a json and return in an http packet
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private void updateFrontEnd() throws IOException {
		//Update the bean
		updateSchedulerInfo();
		
		//Create json for return data
		JSONObject json = new JSONObject();
		json.put("ids" , Arrays.toString(this.bean.getElevatorArray()));
		json.put("floor", Arrays.toString(this.bean.getElevatorFloorArray()));
		json.put("requestTo", Arrays.toString(this.bean.getRequestedFloorArray()));
		json.put("directions", Arrays.toString(this.bean.getDirectionArray()));
		json.put("states", Arrays.toString(this.bean.getStateArray()));
		json.put("numFloors", this.bean.getNumFloors());
		
		//Object outstream from the tcp server
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(webpage.getOutputStream()));
		 	
		//Malform an http packet structure
		out.write("HTTP/1.0 200 OK\r\n");
	    out.write("Date: Fri, 31 Dec 1999 23:59:59 GMT\r\n");
	    out.write("Access-Control-Allow-Origin: *\r\n");
	    out.write("Server: Apache/0.8.4\r\n");
	    out.write("Content-Type: text\r\n");
	    out.write("Content-Length: "+json.toJSONString().length()+"\r\n");
	    out.write("Expires: Sat, 01 Jan 2000 00:59:59 GMT\r\n");
	    out.write("Last-modified: Fri, 09 Aug 1996 14:21:40 GMT\r\n");
	    out.write("\r\n");
	    out.write(json.toJSONString());
	    //Flush the buffer
	    out.flush();
        //Close the connection
		webpage.close();
	}
}
