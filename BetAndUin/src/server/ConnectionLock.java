package server;
/* The lock to check whether the connection is up or down. */
public class ConnectionLock{
	Boolean connectionDown;
	
	public ConnectionLock(){
		connectionDown = true;
	}
	
	public Boolean isConnectionDown(){
		return connectionDown;
	}
	
	public void setConnectionDown(Boolean value){
		connectionDown = value;
	}
}
