package pbHelpers;

import java.net.DatagramPacket;
import java.util.Arrays;
import java.util.logging.Logger;

import com.google.protobuf.InvalidProtocolBufferException;

import elevatorCommands.ElevatorArrivedMessage;
import elevatorCommands.ElevatorDepartureMessage;
import elevatorCommands.ElevatorRequestMessage;
import elevatorCommands.FloorSensorMessage;
import elevatorCommands.LampMessage;
import elevatorCommands.SchedulerDispatchMessage;
import elevatorCommands.WrapperMessage;
import elevators.Elevator;


public class PbMessage {
	private final Logger LOGGER = Logger.getLogger(PbMessage.class.getName());
	WrapperMessage wrapper;
	com.google.protobuf.GeneratedMessageV3 message;
	
	public PbMessage(DatagramPacket message) throws InvalidProtocolBufferException{
		byte[] rawPBArray = cpyDatagramArr(message);
		this.wrapper = getWrapper(rawPBArray); // unpack raw message
		this.unpackMessage(wrapper); // get internal message
	}
	
	// will not be able to parse without this!
	private byte[] cpyDatagramArr(DatagramPacket p){
		return Arrays.copyOfRange(p.getData(), 0, p.getLength());
	}
	
	private WrapperMessage getWrapper(byte[] rawData) throws InvalidProtocolBufferException {
		return WrapperMessage.parseFrom(rawData);
	}
	
	private void unpackMessage(WrapperMessage msg) {
		LOGGER.info("Unpacking message of type " + msg.getMsgCase().toString());
		switch (msg.getMsgCase()) {
			case ELEVATORARRIVED -> this.message = msg.getElevatorArrived();
			case ELEVATORDEPARTURE -> this.message = msg.getElevatorDeparture();
			case ELEVATORREQUEST -> this.message = msg.getElevatorRequest();
			case FLOORSENSOR -> this.message = msg.getFloorSensor();
			case LAMPMESSAGE -> this.message = msg.getLampMessage();
			case SCHEDULERDISPATCH -> this.message = msg.getSchedulerDispatch();
			default -> this.message = null;
		}
	}
	
	public Boolean isElevatorRequestMessage() {
		return this.message instanceof ElevatorRequestMessage;
	}
	
	public Boolean isSchedulerDispatchMessage() {
		return this.message instanceof SchedulerDispatchMessage;
	}

	public Boolean isElevatorArrivedMessage() {
		return this.message instanceof ElevatorArrivedMessage;
	}
	
	public Boolean isElevatorDepartureMessage() {
		return this.message instanceof ElevatorDepartureMessage;
	}
	
	public Boolean isFloorSensorMessage() {
		return this.message instanceof FloorSensorMessage;
	}
	
	public Boolean isLampMessage() {
		return this.message instanceof LampMessage;
	}
 
	
	//TODO: ADD ERROR CHECKING TO CASTS!
	
	public ElevatorRequestMessage toElevatorRequestMessage() {
		return (ElevatorRequestMessage)(this.message);
	}
	
	public SchedulerDispatchMessage toSchedulerDispatchMessage() {
		return (SchedulerDispatchMessage)(this.message);
	}
	
	public ElevatorArrivedMessage toElevatorArrivedMessage() {
		return (ElevatorArrivedMessage)(this.message);
	}
	
	public ElevatorDepartureMessage toElevatorDepartureMessage() {
		return (ElevatorDepartureMessage)(this.message);
	}
	
	public FloorSensorMessage toFloorSensorMessage() {
		return (FloorSensorMessage)(this.message);
	}
	
	public LampMessage toLampMessage() {
		return (LampMessage)(this.message);
	}
}
