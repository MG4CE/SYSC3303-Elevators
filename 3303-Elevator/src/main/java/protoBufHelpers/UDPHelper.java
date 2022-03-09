package protoBufHelpers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Logger;


import elevatorCommands.*;


public abstract class UDPHelper {
	private final Logger LOGGER = Logger.getLogger(UDPHelper.class.getName());
	private DatagramSocket recvSocket;
	private final int PACKET_SIZE = 1024;
	
	public UDPHelper() throws SocketException{
		this.recvSocket = new DatagramSocket();
	}
	
	public UDPHelper(int recvPort) throws SocketException{
		this.recvSocket = new DatagramSocket(recvPort);
	}
	
	public void sendByteArray(byte[] pbMsg, int sendPort) throws IOException {
		DatagramPacket sendPacket = new DatagramPacket(pbMsg, pbMsg.length, InetAddress.getLocalHost(), sendPort);
		DatagramSocket sendSocket = new DatagramSocket();
		sendSocket.send(sendPacket);
		sendSocket.close();
	}
	
	public void sendMessage(com.google.protobuf.GeneratedMessageV3 message, int sendPort) throws IOException {
		LOGGER.info("Sending Protobuf of type " + message.getClass().getName());
		WrapperMessage.Builder msgBldr = WrapperMessage.newBuilder();
		
		// find type of message
		if(message instanceof ElevatorRequestMessage){
			msgBldr.setElevatorRequest((ElevatorRequestMessage) message);
		}else if(message instanceof SchedulerDispatchMessage){
			msgBldr.setSchedulerDispatch((SchedulerDispatchMessage) message);
		}else if(message instanceof ElevatorArrivedMessage){
			msgBldr.setElevatorArrived((ElevatorArrivedMessage) message);
		}else if(message instanceof ElevatorDepartureMessage){
			msgBldr.setElevatorDeparture((ElevatorDepartureMessage) message);
		}else if(message instanceof FloorSensorMessage){
			msgBldr.setFloorSensor((FloorSensorMessage) message);
		}else if(message instanceof LampMessage){
			msgBldr.setLampMessage((LampMessage) message);
		}
		
		WrapperMessage msg = msgBldr.build();
		sendByteArray(msg.toByteArray(), sendPort);
	}	
	
	public DatagramPacket receiveMessage() throws IOException {
		byte[] r = new byte[PACKET_SIZE];
		DatagramPacket rcv = new DatagramPacket(r, PACKET_SIZE);
		this.recvSocket.receive(rcv);
		LOGGER.info("Receiving message from " + Integer.toString(rcv.getPort()));
		return rcv;
	}

	public void closePbSocket(){
		this.recvSocket.close();
	}

}
