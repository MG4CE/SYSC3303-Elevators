package elevators;

import java.util.LinkedList;
import java.util.Queue;


/*
 * Class for the Scheduler, contains synchronized methods for queue
 */
public class Scheduler implements Runnable {
	
	volatile private Queue<Command> commandQueue;
	
	/*
	 * Create new instance of Scheduler
	 */
	public Scheduler() {
		this.commandQueue = new LinkedList<Command>();
	}
	
	/*
	 * Create a new instance of Scheduler
	 * @param Queue of Commands
	 */
	public Scheduler(Queue<Command> commandQueue) {
		this.commandQueue = commandQueue;
	}
	
	/*
	 * Push a new command to the commandQueue
	 * @param Command to be added
	 */
	public synchronized void addCommand(Command cmd) {
		commandQueue.add(cmd);
		notifyAll();
	}
	
	/*
	 * Get latest command from the commandQueue
	 * @return Latest command from the Command queue
	 */
	public synchronized Command getCommand() {
		
		//This is used for later but now we are doing our own scheduling to test
		/**while(commandQueue.isEmpty()) {
			try {
				System.out.println("E waiting");
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
		
		Command msg = commandQueue.poll();
		notifyAll();
		return msg;
	}
	
	public int getCommandQueueSize() {
		return this.commandQueue.size();
	}
		
	@Override
	public void run() {
		//replace true with a done signal?
		System.out.println("Scheduler Start");
		while(!commandQueue.isEmpty()) {
		}
		System.out.println("Schedular done");
	}
}
