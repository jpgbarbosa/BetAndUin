package intraServerCommunication;

public class ChangeStatusLock {
    /* Variable used to check whether the connectionWithServerManager has concluded or not yet, see below. */
    boolean initialProcessConcluded;
    /* Variable to know whether we are the primary server or not. */
    boolean isPrimaryServer;
    boolean changedStatus;
    
    public ChangeStatusLock(){
    	initialProcessConcluded = false;;
        isPrimaryServer = false;
        changedStatus = false;
    }
    
    public boolean hasChangedStatus(){
    	return changedStatus;
    }
    
	public boolean isInitialProcessConcluded() {
		return initialProcessConcluded;
	}

	public boolean isPrimaryServer() {
		return isPrimaryServer;
	}

	public void setInitialProcessConcluded(boolean initialProcessConcluded) {
		this.initialProcessConcluded = initialProcessConcluded;
	}

	public void setPrimaryServer(boolean isPrimaryServer) {
		boolean previousState = this.isPrimaryServer;
		
		this.isPrimaryServer = isPrimaryServer;
		
		/*We changed our status, we ought to inform the server about it. */
		if (previousState != isPrimaryServer){
			changedStatus = true;
		}
		else{
			changedStatus = false;
		}
	}
    
}
