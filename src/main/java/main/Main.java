package main;

import message.ElevatorRegisterMessage;
import message.SchedulerDispatchMessage;
import scheduler.Scheduler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import communication.UDPHelper;
import elevator.Elevator;
import floorsubsystem.FloorSubsystem;

/**
 * Do not run this, used for internal component testing early on (should be removed).
 */
public class Main extends UDPHelper {
	public Elevator e;
	private int elevatorPort; 
	public Main(int recvPort) throws SocketException {
		super(recvPort);
		try {
			this.elevatorPort = 24;
			e = new Elevator(23, InetAddress.getLocalHost(), 24);
		} catch (SocketException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public Main(int recvPort, int elevatorPort) throws SocketException {
		super(recvPort);
		try {
			this.elevatorPort = elevatorPort;
			e = new Elevator(recvPort, InetAddress.getLocalHost(), elevatorPort);
		} catch (SocketException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void sendSchedulerDispatchMessage(int destFloor, int elevatorID) throws IOException {
		SchedulerDispatchMessage msg = SchedulerDispatchMessage.newBuilder()
				.setDestFloor(destFloor)
				.setElevatorID(elevatorID)
				//TODO: SET ELEVATOR ID
				//TODO ADD TIMESTAMP
				.build();
		sendMessage(msg, this.elevatorPort, InetAddress.getLocalHost());
	}
	
	public void sendElevatorRegisterMessage(int elevatorID) throws IOException {
		ElevatorRegisterMessage msg = ElevatorRegisterMessage.newBuilder()
				.setElevatorID(elevatorID)
				.build();

		sendMessage(msg, this.elevatorPort, InetAddress.getLocalHost());
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		Scheduler.main(args);
		Elevator.main(args);
		Elevator.main(args);
		FloorSubsystem.main(args);
		
		/*
		Main m = new Main(23);
		Thread elevator = new Thread(m.e);
		elevator.start();
		Thread.sleep(1000);
		m.sendElevatorRegisterMessage(1);
		Thread.sleep(2000);
		m.sendSchedulerDispatchMessage(0, 123);
		Thread.sleep(5000);
		m.sendSchedulerDispatchMessage(10, 123);
		Thread.sleep(12000);
		m.sendSchedulerDispatchMessage(0, 123);
		Thread.sleep(50000);*/
	}
}
