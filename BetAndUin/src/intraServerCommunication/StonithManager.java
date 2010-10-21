package intraServerCommunication;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;

import common.Constants;

public class StonithManager extends Thread{
	
	/* The STONITH port. */
	private int stonithPort;
	
	public StonithManager(int stPort, int pPort, ConnectionWithServerManager connectionManager){
		stonithPort = stPort;
		new StonithChanger(pPort,connectionManager);
		this.start();
	}
	
	public void run(){
		/* We open the socket connection. */
        ServerSocket listenSocket;
		try {
			/* First, we open the STONITH port.*/
			listenSocket = new ServerSocket(stonithPort);
			if (Constants.DEBUGGING_SERVER){
	        	System.out.println("StonithManager: Listening at port  " + stonithPort);
	        	System.out.println("StonithManager: LISTEN SOCKET=" + listenSocket);
	        }
			while (true){
				/* Then, if the other client tries to connect to this port, 
				 * we will accept the connection to show the other server
				 * that we aren't dead and the problem of not receiving messages
				 * is due to troubles in the link and not to troubles in our
				 * machine.
				 */
				listenSocket.accept(); // BLOQUEANTE
			}
			
		} catch (IOException e) {
			System.out.println("Listen in Stonith Manager:" + e.getMessage());
		}
        
	}
}

/* The thread responsible for changing the ports in case we want to simulate the recovery/
 * failure of the network.
 */
class StonithChanger extends Thread{
	private BufferedReader reader;
	private int initialPort;
	private ConnectionWithServerManager connectionWithServerManager;
	
	public StonithChanger(int port, ConnectionWithServerManager cM){
		initialPort = port;
		connectionWithServerManager = cM;
		this.start();
	}
	
	public void run(){
		reader = new BufferedReader(new InputStreamReader(System.in));
		int port = initialPort;
		
		System.out.println("You are now in the regular scenario.");
		while(true){
			try{
				System.out.println("\nPress enter to change the testing scenarion.");
				reader.readLine();
				
				/* Subtracts one unit to the port because we were at the
				 * STONITH scenario and want to return to the regular one.
				 */
				if (port % 2 == 1){
					port--;
					System.out.println("You are now in the regular scenario.");
				}
				/* Adds one unit to the port because we were at the
				 * regular scenario and want to go to the STONITH one.
				 */
				else{
					port++;
					System.out.println("You are now in the STONITH scenario.");
				}
				
				connectionWithServerManager.setPartnetPort(port);
				
				System.out.println("The port is " + port + ".\n");
				
			}catch(Exception e){
				System.out.println("Exception in StonithChanger: " + e.getMessage());
			}
		}
	}
	
}
