package client;
import java.net.*;
import java.util.StringTokenizer;
import java.io.*;


public class TCPClient {

    public static void main(String args[]) {
		// args[0] <- hostname of destination
		if (args.length == 0) {
		    System.out.println("java TCPClient hostname");
		    //System.exit(0);
		}
	
		/*Set to true if you want the program to display debugging messages.*/
		boolean debugging = true;
		
		/* This is for knowing if we are connecting for the first time or instead, we
		 * are trying to reconnect. It's only use is given a few lines down when we want
		 * to display a message and so it is not necessary for the correct functioning of
		 * the program.
		 */
		boolean firstConnection = false;
		
		/*The socket variable we shall use to connect to the server.*/
		Socket s = null;
		
		/*The variables related to the reconnection.*/
		int retries = 0; //The numbers of times we reconnected already to a given port.
		int retrying = 0; //Tests if we are retrying for the first or second time.
		int WAITING_TIME = 1000; //The time the thread sleeps.
		int NO_RETRIES = 10; //The maximum amount of retries for a given port.
		
		/* Variables used for the login authentication. */
		//String username = "",password = "";
		boolean loggedIn = false;
		
		/*The thread related variables.*/
		ConnectionLock connectionLock = new ConnectionLock();
		ClientWriteTCP writeThread;
		ClientReadTCP readThread;
		
		/*The variables related to the server ports available.*/
		int []serverPorts = new int[2]; //The array with the two different ports.
		int serverPos = 0; //The position array, which corresponds to active port.
		int noServerPorts = serverPorts.length; //Total number of possible servers ports.
		//Places the two ports in the array.
		serverPorts[0] = 6000;
		serverPorts[1] = 7000;
		
		StringTokenizer strToken;
		String command = "";
		
		writeThread =  new ClientWriteTCP(connectionLock);
		readThread = new ClientReadTCP(connectionLock, writeThread);
		writeThread.setReadThread(readThread);
		//Five attempts to reconnect the connection.
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
			    // 1o passo
				//TODO: Make sure you don't forget to change it again to the original, now commented.
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
			    
				if (debugging){
					System.out.println("We passing the socket's reference to our threads.");
				}
			    writeThread.setSocket(s);
			    readThread.setSocket(s);
				
			    String serverAnswer;
				boolean error = false;
				
				/* Login authentication. */
				while(!loggedIn){		
					/* When the server goes down, the client keep the data related to a successful login
					 * When the server is up again, the client application directly sends that information
					 * so the end user won't have to reinsert them once again.
					 */
					if(!(command.equals("")) && !error){
						if (debugging){
							System.out.printf("We already have some data saved (%s).\n", command);
						}
						writeThread.out.writeUTF(command);
						serverAnswer = readThread.in.readUTF();
					}
					/* The information needed for a valid login hasn't been inserted yet. */
					else {
						if (debugging){
							System.out.println("We don't have any login saved.");
						}
						error=false;
			        	System.out.println("to log in: login [user] [pass]\n" +
			        			"to register in: register [user] [pass] [email]");
			        	command = writeThread.reader.readLine();
			        	try{
			        		strToken = new StringTokenizer(command);
			        	}catch(Exception e){
			        		return;
			        	}
			        	
			        	/* temp saves the first token. size gives the number of tokens. */
			        	String temp = strToken.nextToken();
			        	int size = strToken.countTokens();
			        	
			        	/* If the client is registering and hasn't inserted four keywords,
			        	 * or if the client is logging and hasn't inserted three keywords
			        	 * or if he has entered and unknown command, we try again.
			        	 */
			        	if(!((temp.equals("register") && size == 3) 
			    				|| (temp.equals("login") && size == 2))){
			        		System.out.println("Unknown command.");
			        		error=true;
			        		continue;
			    		}
			        	
			        	/* Write into the socket connected to the server. */
			        	writeThread.out.writeUTF(command);
			        	/* Now, it waits for the answer from the server. */
			        	serverAnswer = readThread.in.readUTF();
					}
					
					/* The server informs the client that there was some kind of error in the validation
					 * process. */
		        	if (!serverAnswer.equals("log successful")){
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
		        		
		        		/* Resets the variables. */
		        		serverAnswer = "";
		        		error = true;
		        	}
		        	/* The login has been validated, so the client can now proceed. */
		        	else if (serverAnswer.equals("log successful")){
		        		loggedIn = true;
		        		System.out.println("You are now logged in!");
		        	}
				}
	        	
			    /* Resets the counters, the lock flags and the login variables. */
			    synchronized(connectionLock){
			    	connectionLock.setConnectionDown(false);
			    	connectionLock.notifyAll();
			    	try {
						connectionLock.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			    }
				retries = 0;
				retrying = 0;
				loggedIn = false;
				error = false;
			    
			/* The list of possible exceptions to be handled. */
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
			    	serverPos = (++serverPos)%noServerPorts;
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
			    	serverPos = (++serverPos)%noServerPorts;
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
		 * até que o utilizador prima o Ctr+C.
		 */
		System.out.println("Exited");
		System.exit(0);
    }
}
