package pbHelpers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import com.google.protobuf.Descriptors;
import elevatorCommands.*;
import floorSubsystem.FloorSubsystem;

public abstract class UdpPBHelper {
	private final Logger LOGGER = Logger.getLogger(UdpPBHelper.class.getName());
	int sendPort, recvPort;
	DatagramSocket recvSocket;
	final int PACKET_SIZE = 1024;
	
	public UdpPBHelper(int sendPort, int recvPort) throws SocketException{
		this.sendPort = sendPort;
		this.recvPort = recvPort;
		this.recvSocket = new DatagramSocket(recvPort);
	}
	
	void sendMessage(byte[] pbMsg) throws IOException {
		DatagramPacket sendPacket = new DatagramPacket(pbMsg, pbMsg.length,
											InetAddress.getLocalHost(), this.sendPort);
		DatagramSocket sendSocket = new DatagramSocket();
		sendSocket.send(sendPacket);
		sendSocket.close();
	}
	
	protected void sendMessage(com.google.protobuf.GeneratedMessageV3 message) throws IOException {
		LOGGER.info("Sending Protobuf of type " + message.getClass().getName());
		WrapperMessage.Builder msg = WrapperMessage.newBuilder();
		// find type of message
		if(message instanceof ElevatorRequestMessage){
			msg.setElevatorRequest((ElevatorRequestMessage) message);
		}else if(message instanceof SchedulerDispatchMessage){
			msg.setSchedulerDispatch((SchedulerDispatchMessage) message);
		}else if(message instanceof ElevatorArrivedMessage){
			msg.setElevatorArrived((ElevatorArrivedMessage) message);
		}else if(message instanceof ElevatorDepartureMessage){
			msg.setElevatorDeparture((ElevatorDepartureMessage) message);
		}else if(message instanceof FloorSensorMessage){
			msg.setFloorSensor((FloorSensorMessage) message);
		}else if(message instanceof LampMessage){
			msg.setLampMessage((LampMessage) message);
		}
		msg.build();
		byte[] msgB = message.toByteArray();
		sendMessage(msgB);
	}	
	
	protected DatagramPacket receiveMessage() throws IOException {
		byte[] r = new byte[PACKET_SIZE];
		DatagramPacket rcv = new DatagramPacket(r, PACKET_SIZE);
		this.recvSocket.receive(rcv);
		return rcv;
	}

	protected void closePbSocket(){
		this.recvSocket.close();
	}

}
