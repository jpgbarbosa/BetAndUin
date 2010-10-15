package clientRMI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.*;

import clientTCP.ConnectionLock;

import constants.Constants;

import server.ClientOperations;

public class RMIClient extends UnicastRemoteObject implements ServerOperations{

	private static final long serialVersionUID = 1L;
	
	public RMIClient() throws RemoteException {
		super();
	}
	
	public static void main(String args[]) {
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String [] stringSplitted = null;
		String username="",password="";
		String serverAnswer = "";
				
		boolean loggedIn = false;
		ClientOperations server = null;
		
		/*Set to true if you want the program to display debugging messages.*/
		boolean debugging = false;
		
		/* This is for knowing if we are connecting for the first time or instead, we
		 * are trying to reconnect. It's only use is given a few lines down when we want
		 * to display a message and so it is not necessary for the correct functioning of
		 * the program.
		 */
		boolean firstConnection = false;
		
		/*The variables related to the reconnection.*/
		int retries = 0; //The numbers of times we reconnected already to a given port.
		int retrying = 0; //Tests if we are retrying for the first or second time.
		int WAITING_TIME = 1000; //The time the thread sleeps.
		int NO_RETRIES = 10; //The maximum amount of retries for a given port.
		
		/*The variables related to the server ports available.*/
		int []serverPorts = new int[2]; //The array with the two different ports.
		int serverPos = 0; //The position array, which corresponds to active port.
		int noServerPorts = serverPorts.length; //Total number of possible servers ports.
		//Places the two ports in the array.
		serverPorts[0] = Constants.FIRST_RMI_SERVER_PORT;
		serverPorts[1] = Constants.SECOND_RMI_SERVER_PORT;
		
		ConnectionLock connectionLock = new ConnectionLock();
		
		while (retries < NO_RETRIES){
			try {			
				/* We haven't retried yet, so, it's useless to sleep for WAITING_TIME milliseconds. */
				if (retrying > 0){
				    try {
						Thread.sleep(WAITING_TIME);
					} catch (InterruptedException e) {
						System.out.println("This thread was interrupted while sleeping.\n");
						System.exit(0);
					}
				}

				RMIClient client = new RMIClient();
				server = (ClientOperations) Naming.lookup("rmi://localhost:" + serverPorts[serverPos] +"/BetAndUinServer");
			
				if (!firstConnection){
					System.out.println("Connected to server in port " + serverPorts[serverPos] + ".");
					firstConnection = true;
				}
				else{
					System.out.println("Has successfully reconnected to server, now in port " + serverPorts[serverPos] + ".");
				}
				
				while (!loggedIn){
					/* The user hasn't made a successful login yet. */
					if (username.equals("") && password.equals("")){
		                /* Reads and splits the input. */
						System.out.println("\nTo log in: login [user] [pass]\n" +
	        			"To register in: register [user] [pass] [email]");
						stringSplitted = reader.readLine().split(" ");
		                	
		                if(stringSplitted.length == 4 && stringSplitted[0].equals("register")){
		                	username = stringSplitted[1];
		                	password = stringSplitted[2];
		                	String mail = stringSplitted[3];
		                	
		                	serverAnswer = server.clientRegister(username, password, mail, (ServerOperations) client);
		                	
		                } else if(stringSplitted.length == 3 && stringSplitted[0].equals("login")){
		                	username = stringSplitted[1];
		                	password = stringSplitted[2];
			                
		                	serverAnswer = (String) server.clientLogin(username, password, client);
		                }
		                
			        	if (!serverAnswer.equals("log successful")){
			        		username = "";
			        		password = "";
			        		
			        		/* This client isn't registered in the system. */
			        		if (serverAnswer.equals("log error")){
			        			System.out.println("\nUsername or password incorrect. Please try again...\n");
			        		}
			        		/* This client is logged in in another machine. */
			        		else if (serverAnswer.equals("log repeated")){
			        			System.out.println("\nSorry, but this user is already logged in...\n");
			        		}
			        		/* This username is already taken by another client. */
			        		else if (serverAnswer.equals("log taken")){
			        			System.out.println("\nSorry, but this username isn't available, choose another.\n");
			        		}
			        		else if (serverAnswer.equals("username all")){
			        			System.out.println("Sorry, but the keyword 'all' is reserved, pick another name.");
			        		}
			        		else if(serverAnswer.equals("user not registed")){
			        			System.out.println("Sorry, but you aren't registed yet. Please use command" +
			        					"register [user] [pass] [mail]");
			        		}
			        	} else {
			        		System.out.println("You're now logged in!");
			        		/* Shows the main menu. */
			        		System.out.println(server.clientShowMenu());
			        		loggedIn = true;
			        	}
			        	
					}
					/* The client is logged in already. */
					else{
						serverAnswer = server.clientLogin(username, password, (ServerOperations) client);
						
					}
				} //while(!loggedIn)
				
				retries = 0;
				retrying = 0;
				loggedIn = true;
				
				//TODO: rever aqui se existem comandos por executar?
				
				synchronized(connectionLock){
			    	connectionLock.setConnectionDown(false);
			    	connectionLock.notifyAll();
			    	try {
						connectionLock.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			    }
			    
			/* The list of possible exceptions to be handled. */
			} catch (NotBoundException e) {
				if (debugging){
					System.out.println("NotBoundException:" + e.getMessage());
				}
			}catch (RemoteException e) {
				if (debugging){
					System.out.println("RemoteException:" + e.getMessage());
				}
			    retries++;
			    /* In this case, it's the first connection, and the server is already down.
			     * So, we will pass immediately to the next one and retry this one later.
			     */
			    if (retries == 1 && retrying == 0){
			    	serverPos = (++serverPos)%noServerPorts;
			    	if (debugging){
			    		System.out.println("Connection Lost... Trying to connect to server in port " + serverPorts[serverPos] + ".");
			    	}
			    }
			    /* We have retried the connection at least once.
			     * Consequently, the thread shall wait WAITING_TIME milliseconds 
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
			    	serverPos = (++serverPos)%noServerPorts;
			    	System.out.println("Trying to connect to server in port " + serverPorts[serverPos] + ".");
			    	retrying++;
			    }
			} catch (IOException e) {
				if (debugging){
					System.out.println("IOException:" + e.getMessage());
				}
			}
		}
		
		//TODO: Perguntar porque é que sem isto o programa não termina.
		System.out.println("Exited");
		System.exit(0);
	}

	public void printUserMessage(String msg) throws java.rmi.RemoteException{
		System.out.println(msg);
	}
	    
    @Override
    public boolean testUser() throws java.rmi.RemoteException{
    	return true;
    }
}
