package scheduler;


import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.simple.JSONObject;

import message.Direction;

public class SchedulerTCPServer  implements Runnable {
	private  String OUTPUT;
	private static final String OUTPUT_HEADERS = "HTTP/1.1 200 OK\r\n" +
	    "Content-Type: text/html\r\n" + 
	    "Content-Length: ";
	private static final String OUTPUT_END_OF_HEADERS = "\r\n\r\n";

	private int socketNum = 107;
	private Socket webpage;
	private ServerSocket server;
	private boolean running = true;
	private Scheduler scheduler;
	private OutputStream output;
	BufferedWriter o;
	SchedulerBean bean;
	int numberOfElevators = 0;

	
	public SchedulerTCPServer(Scheduler scheduler) throws IOException {
		try {
			this.scheduler = scheduler;
			server = new ServerSocket(socketNum);
			this.bean = new SchedulerBean();
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
				bean.getNumberOccupentsList().set(index, 1);
				bean.setNumFloors(scheduler.numFloors);
				bean.getElevatorStateList().set(index, translateState(elevator));
				bean.getDirectionList().set(index, translateDirection(elevator.getCurrentDestination(), elevator.getCurrentFloor()));
			}
			else {
				//New elevator add its defaults
				bean.getElevatorList().add(elevator.getElevatorID());
				bean.getElevatorFloorList().add(elevator.getCurrentFloor()+1);
				bean.getFloorRequestedList().add(elevator.getCurrentDestination()+1);
				bean.getNumberOccupentsList().add(1);
				bean.setNumFloors(scheduler.numFloors);
				bean.getElevatorStateList().add(0);
				bean.getDirectionList().add(0);
				this.numberOfElevators++;
			}
			checkForHardFaults(this.scheduler.getElevatorControl());
		}
		bean.buildArrays();
	}
	
	
	private void checkForHardFaults(ArrayList<ElevatorControl> elevatorControl) {
		boolean found = false;
		int index = -1;
		
		for(Integer elevatorIdThatHF: this.bean.getHardFaultList()) {
			if((index = this.bean.findElevatorIndex(elevatorIdThatHF))!=-1) {
				this.bean.getElevatorStateList().set(index, 9);
			}
		}
	}

	public int translateState(ElevatorControl ev) {
		if(ev.getState().equals(ElevatorControl.ElevatorState.STOPPED) & ev.getCurrentFloor() != ev.getCurrentDestination()) {
			return 1;
		}else {
			
		}
		return ev.getState().ordinal();
	}
	
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
	
	public void runServer() throws IOException {
		    listenForCall();
			updateFrontEnd();
	}
	
	private void listenForCall() throws IOException {
		webpage = server.accept();
	}
	
	private void updateFrontEnd() throws IOException {
		//Get the updated bean
		updateSchedulerInfo();
		JSONObject json = new JSONObject();
		json.put("ids" , Arrays.toString(this.bean.getElevatorArray()));
		json.put("floor", Arrays.toString(this.bean.getElevatorFloorArray()));
		json.put("requestTo", Arrays.toString(this.bean.getRequestedFloorArray()));
		json.put("occupents", Arrays.toString(this.bean.getNumberOccupentsArray()));
		json.put("directions", Arrays.toString(this.bean.getDirectionArray()));
		json.put("states", Arrays.toString(this.bean.getStateArray()));
		json.put("numFloors", this.bean.getNumFloors());
		
		 BufferedWriter out = new BufferedWriter(new OutputStreamWriter(webpage.getOutputStream()));
		
		 	
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

	     out.flush();
        
		webpage.close();
	}

}
