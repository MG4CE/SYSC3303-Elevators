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
import java.util.Arrays;

import org.json.simple.JSONObject;

public class SchedulerTCPServer  implements Runnable {
	private  String OUTPUT;
	private static final String OUTPUT_HEADERS = "HTTP/1.1 200 OK\r\n" +
	    "Content-Type: text/html\r\n" + 
	    "Content-Length: ";
	private static final String OUTPUT_END_OF_HEADERS = "\r\n\r\n";

	private int socketNum = 2099;
	private Socket webpage;
	private ServerSocket server;
	private boolean running = true;
	private Scheduler scheduler;
	private OutputStream output;
	BufferedWriter o;
	SchedulerBean bean;

	
	public SchedulerTCPServer(Scheduler scheduler) throws IOException {
		try {
			this.scheduler = scheduler;
			server = new ServerSocket(105);
			this.bean = new SchedulerBean();
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
				System.out.println("System running");
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
	
	protected void updateSchedulerInfo() {
		
		System.out.println(this.scheduler.getElevatorControl().size());
		for (ElevatorControl elevator : this.scheduler.getElevatorControl()) {
			int index;
			System.out.println(elevator.getElevatorID());
			System.out.println(bean.findElevatorIndex(elevator.getElevatorID()));
			System.out.println(Arrays.toString(bean.getElevatorList().toArray()));
			
			if((index = bean.findElevatorIndex(elevator.getElevatorID())) != -1) {
				bean.getElevatorFloorList().set(index, elevator.getCurrentFloor());
				bean.getFloorRequestedList().set(index, elevator.getCurrentDestination());
				bean.getNumberOccupentsList().set(index, 1);
			}
			else {
				bean.getElevatorList().add(elevator.getElevatorID());
				bean.getElevatorFloorList().add(elevator.getCurrentFloor());
				bean.getFloorRequestedList().add(elevator.getCurrentDestination());
				bean.getNumberOccupentsList().add(1);
			}
		}
		bean.buildArrays();
		
	}
	
	public void runServer() throws IOException {
		    listenForCall();
			updateFrontEnd();
	}
	
	private void listenForCall() throws IOException {
		System.out.println("Waiting for call");
		webpage = server.accept();
		System.out.println("Webpage call");
		
	}
	
	private void updateFrontEnd() throws IOException {
		System.out.println("Updating front end");
		updateSchedulerInfo();
		JSONObject json = new JSONObject();
		json.put("ids" , Arrays.toString(this.bean.getElevatorArray()));
		json.put("floor", Arrays.toString(this.bean.getElevatorFloorArray()));
		json.put("requestTo", Arrays.toString(this.bean.getRequestedFloorArray()));
		json.put("occupents", Arrays.toString(this.bean.getNumberOccupentsArray()));
		
		System.out.println((this.scheduler.getElevatorControl().size()));
		
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
