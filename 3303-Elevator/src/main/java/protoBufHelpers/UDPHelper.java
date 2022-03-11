package protoBufHelpers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.logging.Logger;


import elevatorCommands.*;

/*
 * Helper class to be extended by any class that sends/receives messages
 */
public abstract class UDPHelper {
	private final Logger LOGGER = Logger.getLogger(UDPHelper.class.getName());
	private final DatagramSocket recvSocket;
	private final int PACKET_SIZE = 1024;
	private final int TIMEOUT = 5;
	private final int MAX_NUM_RETRIES = 10;

	/*
	 * Constructor to be called with super(), will create a listen socket
	 */
	public UDPHelper() throws SocketException{
		this.recvSocket = new DatagramSocket();
	}

	/*
	 * Constructor to be called with super(), will create a listen socket bound to recvPort
	 * @param port to listen to
	 */
	public UDPHelper(int recvPort) throws SocketException{
		this.recvSocket = new DatagramSocket(recvPort);
	}

	/*
	 * Send a raw byte array to a destination port
	 * @param data byte array to be send via udp
	 * @param port to send to
	 */
	public void sendByteArray(byte[] data, int sendPort) throws IOException {
		DatagramPacket sendPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), sendPort);
		DatagramSocket sendSocket = new DatagramSocket();
		sendSocket.send(sendPacket);
		sendSocket.close();
	}
	
	public DatagramPacket rpcSend(byte[] sendData, Boolean useTimeout, int sendPort) throws IOException {
		byte data_buff[] = new byte[PACKET_SIZE];
		DatagramPacket receivePacket = new DatagramPacket(data_buff, data_buff.length);
		Boolean received = false;
		int numRetries = 0;
		
		if(!useTimeout) {
			try {
				recvSocket.setSoTimeout(0);
			} catch (SocketException e) {
				e.printStackTrace();
				LOGGER.severe("Failed to change port timeout!");
				return null;
			}
		} else {
			try {
				recvSocket.setSoTimeout(TIMEOUT * 1000);
			} catch (SocketException e) {
				e.printStackTrace();
				LOGGER.severe("Failed to change port timeout!");
				return null;
			}
		}
		
		while (!received && numRetries < MAX_NUM_RETRIES) {
			sendByteArray(sendData, sendPort);
			try {
				recvSocket.receive(receivePacket);
			} catch (SocketTimeoutException e) {
				LOGGER.warning("Host did not respond, retrying!");
				numRetries++;
				continue;
			}
			LOGGER.info("Receiving message from " + Integer.toString(receivePacket.getPort()));
			received = true;
		}
		
		if (numRetries >= MAX_NUM_RETRIES) {
			LOGGER.warning("Retry limited reached!");
			return null;
		}
		
		recvSocket.setSoTimeout(0);
		return receivePacket;
	}
	
	
	public DatagramPacket rpcSendMessage(com.google.protobuf.GeneratedMessageV3 message, int sendPort) throws IOException {
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
		return rpcSend(msg.toByteArray(), true, sendPort);
	}	
	
	/*
	 * Send a message of type protobufmessage (Any of defined messages) Will wrap message in WrapperMessage first
	 * @param Protobuf message to send
	 * @param port to send message to
	 */
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
		}else if(message instanceof ElevatorRegisterMessage) {
			msgBldr.setRegisterMessage((ElevatorRegisterMessage)message);
		}
		
		WrapperMessage msg = msgBldr.build();
		sendByteArray(msg.toByteArray(), sendPort);
	}	

	/*
	 * Blocking call to receive a message, returns it datagram packet
	 * @return datagram packet received from listen socket
	 */
	public DatagramPacket receiveMessage() throws IOException {
		byte[] r = new byte[PACKET_SIZE];
		DatagramPacket rcv = new DatagramPacket(r, PACKET_SIZE);
		this.recvSocket.receive(rcv);
		LOGGER.info("Receiving message from " + Integer.toString(rcv.getPort()));
		return rcv;
	}

	/*
	 * Close listen socket
	 */
	public void closePbSocket(){
		this.recvSocket.close();
	}

}
