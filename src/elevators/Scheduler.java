package elevators;

import java.util.LinkedList;
import java.util.Queue;


/*
 * Class for the Scheduler, contains synchronized methods for queue
 */
public class Scheduler implements Runnable {
	
	private Queue<Command> commandQueue;
	
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
		return msg;
	}
		
	@Override
	public void run() {
		//replace true with a done signal?
		while(true);
	}
}
