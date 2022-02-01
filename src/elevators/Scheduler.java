package elevators;

import java.util.LinkedList;
import java.util.Queue;


/*
 * Class for the Scheduler, contains synchronized methods for queue
 */
public class Scheduler implements Runnable {
	
	private Queue<Message> commandQueue;
	
	/*
	 * Create new instance of Scheduler
	 */
	public Scheduler() {
		this.commandQueue = new LinkedList<Message>();
	}
	
	/*
	 * Create a new instance of Scheduler
	 * @param Queue of messages
	 */
	public Scheduler(Queue<Message> commandQueue) {
		this.commandQueue = commandQueue;
	}
	
	/*
	 * Push a new command to the commandQueue
	 * @param Message to be added
	 */
	public synchronized void addCommand(Message msg) {
		commandQueue.add(msg);
		notifyAll();
	}
	
	/*
	 * Get latest command from the commandQueue
	 * @return Latest command from the message queue
	 */
	public synchronized Message getCommand() {
		
		while(commandQueue.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		Message msg = commandQueue.remove();
		notifyAll();
		return msg;
	}
		
	@Override
	public void run() {
		//replace true with a done signal?
		while(true);
	}
}
