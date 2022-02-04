package elevators;

import java.util.LinkedList;
import java.util.Queue;


/**
 * Class for the Scheduler, contains synchronized methods for queue
 */
public class Scheduler implements Runnable {
	
	volatile private Queue<Command> commandQueue;
	volatile private Boolean terminateTread;
	
	/**
	 * Create new instance of Scheduler
	 */
	public Scheduler() {
		this.commandQueue = new LinkedList<Command>();
		terminateTread = false;
	}
	
	/**
	 * Create a new instance of Scheduler
	 * @param Queue of Commands
	 */
	public Scheduler(Queue<Command> commandQueue) {
		this.commandQueue = commandQueue;
	}
	
	/**
	 * Push a new command to the commandQueue
	 * @param Command to be added
	 */
	public synchronized void addCommand(Command cmd) {
		commandQueue.add(cmd);
		notifyAll();
	}
	
	/**
	 * Get latest command from the commandQueue
	 * @return Latest command from the Command queue
	 */
	public synchronized Command getCommand() {
		while(commandQueue.isEmpty()) {
			try {
 				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		Command msg = commandQueue.remove();
		notifyAll();
		if (msg.getFloor() == -1) {
			terminateTread = true;
		}
		return msg;
	}
	
	public int getCommandQueueSize() {
		return this.commandQueue.size();
	}
		
	@Override
	public void run() {
		while(!terminateTread);
		System.out.println("Schedular terminated");
	}
}
