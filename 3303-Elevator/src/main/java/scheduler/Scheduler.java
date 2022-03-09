package scheduler;

import elevatorCommands.SchedulerDispatchMessage;
import protoBufHelpers.ProtoBufMessage;
import protoBufHelpers.UDPHelper;
import stateMachine.StateMachine;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.logging.Logger;

public class Scheduler extends UDPHelper implements Runnable {
    private final Logger LOGGER = Logger.getLogger(Scheduler.class.getName());
    private Boolean running = true;
    private final StateMachine schedulerFSM;

    public Scheduler(int recvPort) throws SocketException {
        super(recvPort);
        LOGGER.info("Initializing scheduler");
        this.schedulerFSM = new StateMachine(null); // todo: ADD STATES
    }

    void sendSchedulerDispatchMessage(int destFloor, int elevatorID) throws IOException {
        LOGGER.info("Dispatching elevator " + Integer.toString(elevatorID) + "to floor " +
                Integer.toString(destFloor));
        SchedulerDispatchMessage msg = SchedulerDispatchMessage.newBuilder()
                .setDestFloor(destFloor)
                .setElevatorID(elevatorID)
                // TODO: SET ELEVATOR ID
                //TODO ADD TIMESTAMP
                .build();
        sendMessage(msg, 0);
    }

    @Override
    public void run() {
        while(this.running){
            try {
                DatagramPacket recvMessage = receiveMessage(); // wait for message from scheduler
                this.schedulerFSM.updateFSM(new ProtoBufMessage(recvMessage)); // update fsm
            }catch (IOException e){
                this.running = false;
                break;
            }
        }
        LOGGER.info("Scheduler done running");
        closePbSocket();
    }
}
