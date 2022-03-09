package scheduler;

import elevatorCommands.SchedulerDispatchMessage;
import pbHelpers.PbMessage;
import pbHelpers.UdpPBHelper;
import stateMachine.StateMachine;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.logging.Logger;

public class Scheduler extends UdpPBHelper implements Runnable {
    private final Logger LOGGER = Logger.getLogger(Scheduler.class.getName());
    private Boolean running = true;
    private final StateMachine schedulerFSM;

    public Scheduler(int sendPort, int recvPort) throws SocketException {
        super(sendPort, recvPort);
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
        sendMessage(msg);
    }

    @Override
    public void run() {
        while(this.running){
            try {
                DatagramPacket recvMessage = receiveMessage(); // wait for message from scheduler
                this.schedulerFSM.updateFSM(new PbMessage(recvMessage)); // update fsm
            }catch (IOException e){
                this.running = false;
                break;
            }
        }
        LOGGER.info("Scheduler done running");
        closePbSocket();
    }
}
