package elevators;



/**
 * Provides away to send a boolean signal 
 * between threads that hold the signal.
 * 
 * 
 * 
 */
public class Signal {
	    
    private Boolean signal;
	    
    public Signal() {
	   signal = false;
	}
	    
    public synchronized void setSignal(Boolean val) {
	   signal = val;
	}
	    
	public synchronized Boolean getSignal() {
	   return signal;
	}
	    
}

