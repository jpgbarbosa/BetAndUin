import java.net.*;
import java.io.*;


public class TCPClient {
    public static void main(String args[]) {
	// args[0] <- hostname of destination
	if (args.length == 0) {
	    System.out.println("java TCPClient hostname");
	    //System.exit(0);
	}

	/*Set to true if you want the program to display debugging messages.*/
	Boolean debugging = false;
	
	Boolean firstConnection = false;
	
	/*The socket variable we shall use to connect to the server.*/
	Socket s = null;
	
	/*The variables related to the reconnection.*/
	int retries = 0; //The numbers of times we reconnected already to a given port.
	int retrying = 0; //Tests if we are retrying for the first or second time.
	int WAITING_TIME = 1000; //The time the thread sleeps.
	int NO_RETRIES = 10; //The maximum amount of retries for a given port.
	
	/*The thread related variables.*/
	ConnectionLock connectionLock = new ConnectionLock();
	ClientWriteTCP writeThread;
	ClientReadTCP readThread;
	
	/*The variables related to the server ports available.*/
	int []serverPorts = new int[2]; //The array with the two different ports.
	int serverPos = 0; //The position array, which corresponds to active port.
	//Places the two ports in the array.
	serverPorts[0] = 6000;
	serverPorts[1] = 7000;
	
	writeThread =  new ClientWriteTCP(connectionLock);
	readThread = new ClientReadTCP(connectionLock);
	//Five attemps to reconnect the connection.
	while (retries < NO_RETRIES){
		try {
			
			/* We haven't retried yet, so, it's useless to sleep for WAITING_TIME miliseconds. */
			if (retrying > 0){
			    try {
					Thread.sleep(WAITING_TIME);
				} catch (InterruptedException e) {
					//TODO Auto-generated catch block
					e.printStackTrace();
					System.exit(0);
				}
			}
		    // 1o passo
		    //s = new Socket(args[0], serversocket);
			s = new Socket("localHost", serverPorts[serverPos]);
			
			if (debugging){
				System.out.println("SOCKET=" + s);
			}
			
			if (!firstConnection){
				System.out.println("Connected to server in port " + serverPorts[serverPos] + ".");
				firstConnection = true;
			}
			else{
				System.out.println("Has successfully reconnected to server, now in port " + serverPorts[serverPos] + ".");
			}
		    
		    writeThread.setSocket(s);
		    readThread.setSocket(s);
		    
		    // 3o passo
	
		    //Resets the counters and the lock flags.
		    synchronized(connectionLock){
		    	connectionLock.setConnectionDown(false);
		    	connectionLock.notifyAll();
		    	try {
					connectionLock.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
			retries = 0;
			retrying = 0;
		    
		} catch (UnknownHostException e) {
			if (debugging){
				System.out.println("Sock:" + e.getMessage());
			}
		} catch (EOFException e) {
			if (debugging){
				System.out.println("EOF:" + e.getMessage());
			}
		} catch (IOException e) {
			if (debugging){
				System.out.println("IO:" + e.getMessage());
			}
		    retries++;
		    /* In this case, it's the first connection, and the server is already down.
		     * So, we will pass immediately to the next one and retry this one later.
		     */
		    if (retries == 1 && retrying == 0){
		    	serverPos = (++serverPos)%2;
		    	if (debugging){
		    		System.out.println("Trying to connect to server in port " + serverPorts[serverPos] + ".");
		    	}
		    }
		    /* We have retried the connection at least once.
		     * Consequently, the thread shall wait WAITING_TIME miliseconds 
		     * when restarts the while cycle.
		     */
		    else if (retrying == 0){
		    	retrying = 1;
		    }
		    /* We have completed one round of retries for one server.
		     * Therefore, we shall try now the second server.
		     */
		    else if (retries == 10 && retrying == 1){
		    	//Resets the number of retries and passes to the serverPort of the other server.
		    	retries = 0;
		    	serverPos = (++serverPos)%2;
		    	System.out.println("Trying to connect to server in port " + serverPorts[serverPos] + ".");
		    	retrying++;
		    }
		} finally {
		    if (s != null)
			try {
			    s.close();
			} catch (IOException e) {
			    System.out.println("close:" + e.getMessage());
			}
		}
		}
	
		/* Talvez temos de alterar isto, para deixar o programa a correr indefinidamente
		 * at� que o utilizador prima o Ctr+C.
		 */
		System.out.println("Exited");
		System.exit(0);
    }
    
}
