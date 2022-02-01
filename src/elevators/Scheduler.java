package elevators;

import java.util.Queue;

public class Scheduler implements Runnable {
	
	private Queue<Message> commandQueue;
	
	public Scheduler(Queue<Message> commandQueue) {
		this.commandQueue = commandQueue;
	}
	
	public synchronized void addCommand(Message msg) {
		commandQueue.add(msg);
		notifyAll();
	}
	
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
