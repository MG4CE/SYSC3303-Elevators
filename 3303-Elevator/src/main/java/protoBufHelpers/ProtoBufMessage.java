package protoBufHelpers;

import java.net.DatagramPacket;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;

import com.google.protobuf.InvalidProtocolBufferException;

import elevatorCommands.ElevatorArrivedMessage;
import elevatorCommands.ElevatorDepartureMessage;
import elevatorCommands.ElevatorRegisterMessage;
import elevatorCommands.ElevatorRequestMessage;
import elevatorCommands.FaultMessage;
import elevatorCommands.FloorSensorMessage;
import elevatorCommands.LampMessage;
import elevatorCommands.SchedulerDispatchMessage;
import elevatorCommands.SimulateFaultMessage;
import elevatorCommands.WrapperMessage;


/**
 * Class that provides Java wrapper for protobuf messages, used to unpack wrapper message, get type of message,
 * as well as cast to specific type.
 */
public class ProtoBufMessage {
	private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(ProtoBufMessage.class);
	WrapperMessage wrapper;
	com.google.protobuf.GeneratedMessageV3 message;

	/**
	 * Create a new protobuf message type, takes in a DatagramPacket and unpacks wrapper
	 * 
	 * @pararm message DatagramPacket received from socket.receive()
	 */
	public ProtoBufMessage(DatagramPacket message) throws InvalidProtocolBufferException{
		byte[] rawPBArray = cpyDatagramArr(message);
		this.wrapper = getWrapper(rawPBArray); // unpack raw message
		this.unpackMessage(wrapper); // get internal message
	}

	/**
	 * Create a new protobuf message type, takes in a WrapperMessage (defined by proto), unpacks its contents
	 * 
	 * @param WrapperMesage
	 */
	public ProtoBufMessage(WrapperMessage wrapperMessage){
		this.wrapper = wrapperMessage;
		this.unpackMessage(wrapper);
	}

	/**
	 * Copy protobuf wrapper message from raw bytes array
	 * 
	 * @param Datagram packet to parse
	 * @return bytes array containing protobuf message
	 */
	private byte[] cpyDatagramArr(DatagramPacket p){
		return Arrays.copyOfRange(p.getData(), 0, p.getLength());
	}

	/**
	 * Parse the wrappermessaeg from the raw bytes array
	 * 
	 * @param byte array holding protobuf wrapper message
	 * @return WrapperMessage received from byte array
	 */
	private WrapperMessage getWrapper(byte[] rawData) throws InvalidProtocolBufferException {
		return WrapperMessage.parseFrom(rawData);
	}

	/**
	 * Unpack a wrapper message into its corresponding message type (types defined in elevator.proto)
	 * Store message as parent class and save as field
	 * 
	 * @param WrapperMessage to unpack
	 */
	private void unpackMessage(WrapperMessage msg) {
		LOGGER.debug("Unpacking message of type " + msg.getMsgCase().toString());
		switch (msg.getMsgCase()) {
			case ELEVATORARRIVED -> this.message = msg.getElevatorArrived();
			case ELEVATORDEPARTURE -> this.message = msg.getElevatorDeparture();
			case ELEVATORREQUEST -> this.message = msg.getElevatorRequest();
			case FLOORSENSOR -> this.message = msg.getFloorSensor();
			case LAMPMESSAGE -> this.message = msg.getLampMessage();
			case SCHEDULERDISPATCH -> this.message = msg.getSchedulerDispatch();
			case REGISTERMESSAGE -> this.message = msg.getRegisterMessage();
			case FAULTMESSAGE -> this.message = msg.getFaultMessage();
			default -> this.message = null;
		}
	}

	/**
	 * Check if message of type ElevatorRequestMessage
	 * 
	 * @return True if of type
	 */
	public Boolean isElevatorRequestMessage() {
		return this.message instanceof ElevatorRequestMessage;
	}

	/**
	 * Check if message of type SchedulerDispatchMessage
	 * 
	 * @return True if of type
	 */
	public Boolean isSchedulerDispatchMessage() {
		return this.message instanceof SchedulerDispatchMessage;
	}

	/**
	 * Check if message of type ElevatorFaultMessage
	 * 
	 * @return True if of type
	 */
	public Boolean isElevatorFaultMessage() {
		return this.message instanceof FaultMessage;
	}
	
	/**
	 * Check if message of type ElevatorSimulateFaultMessage
	 * 
	 * @return True if of type
	 */
	public Boolean isElevatorSimulateFaultMessage() {
		return this.message instanceof SimulateFaultMessage;
	}
	
	/**
	 * Check if message of type ElevatorArrivedMessage
	 * 
	 * @return True if of type
	 */
	public Boolean isElevatorArrivedMessage() {
		return this.message instanceof ElevatorArrivedMessage;
	}

	/**
	 * Check if message of type ElevatorDepartureMessage
	 * 
	 * @return True if of type
	 */
	public Boolean isElevatorDepartureMessage() {
		return this.message instanceof ElevatorDepartureMessage;
	}

	/**
	 * Check if message of type FloorSensorMessage
	 * 
	 * @return True if of type
	 */
	public Boolean isFloorSensorMessage() {
		return this.message instanceof FloorSensorMessage;
	}

	/**
	 * Check if message of type LampMessage
	 * 
	 * @return True if of type
	 */
	public Boolean isLampMessage() {
		return this.message instanceof LampMessage;
	}
	
	/**
	 * Check if message of type ElevatorRegisterMessage
	 * 
	 * @return True if of type
	 */
	public Boolean isElevatorRegisterMessage() {
		return this.message instanceof ElevatorRegisterMessage;
	}
 
	//TODO: ADD ERROR CHECKING TO CASTS!

	/**
	 * Cast message to type ElevatorRequestMessage
	 * 
	 * @return message of type ElevatorRequestMessage
	 */
	public ElevatorRequestMessage toElevatorRequestMessage() {
		return (ElevatorRequestMessage)(this.message);
	}

	/**
	 * Cast message to type ElevatorRequestMessage
	 * 
	 * @return message of type SchedulerDispatchMessage
	 */
	public SchedulerDispatchMessage toSchedulerDispatchMessage() {
		return (SchedulerDispatchMessage)(this.message);
	}
	
	/**
	 * Cast message to type ElevatorFaultMessage
	 * 
	 * @return message of type ElevatorFaultMessage
	 */
	public FaultMessage toElevatorFaultMessage() {
		return (FaultMessage)(this.message);
	}
	
	/**
	 * Cast message to type SimulateFaultMessage
	 * 
	 * @return message of type SimulateFaultMessage
	 */
	public SimulateFaultMessage toElevatorSimulateFaultMessage() {
		return (SimulateFaultMessage)(this.message);
	}
	
	/**
	 * Cast message to type ElevatorRequestMessage
	 * 
	 * @return message of type ElevatorArrivedMessage
	 */
	public ElevatorArrivedMessage toElevatorArrivedMessage() {
		return (ElevatorArrivedMessage)(this.message);
	}

	/**
	 * Cast message to type ElevatorRequestMessage
	 * 
	 * @return message of type ElevatorDepartureMessage
	 */
	public ElevatorDepartureMessage toElevatorDepartureMessage() {
		return (ElevatorDepartureMessage)(this.message);
	}

	/**
	 * Cast message to type ElevatorRequestMessage
	 * 
	 * @return message of type FloorSensorMessage
	 */
	public FloorSensorMessage toFloorSensorMessage() {
		return (FloorSensorMessage)(this.message);
	}

	/**
	 * Cast message to type ElevatorRequestMessage
	 * 
	 * @return message of type LampMessage
	 */
	public LampMessage toLampMessage() {
		return (LampMessage)(this.message);
	}

	/**
	 * Cast message to type ElevatorRegisterMessage
	 * 
	 * @return message of type ElevatorRegisterMessage
	 */
	public ElevatorRegisterMessage toElevatorRegisterMessage() {
		return (ElevatorRegisterMessage)(this.message);
	}
}
