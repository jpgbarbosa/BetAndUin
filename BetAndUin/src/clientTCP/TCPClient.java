/* By:
 * 		Ivo Daniel Venhuizen Correia, no 2008110814
 * 		João Pedro Gaioso Barbosa, no 2008111830
 * 
 * Distributed Systems, October 2010 
 */

package clientTCP;

import java.net.*;
import java.util.Vector;
import java.io.*;

import common.ConnectionLock;
import common.Constants;


public class TCPClient {
	
	/* This is for knowing if we are connecting for the first time or instead, we
	 * are trying to reconnect. It's only use is given a few lines down when we want
	 * to display a message and so it is not necessary for the correct functioning of
	 * the program.
	 */
	private boolean firstConnection = false;
	
    /* This variable is used when the user tries to reset the number of credits.
     * The other thread will ask for the server to inform the client system about
     * the amount of credits it has in order to prevent (or better saying, inform)
     * the client from losing credits. Consequently, we can't print to the screen
     * the information related to this step that is transparent to the end user.
     */
    private boolean isToPrint = true;
	
    /* In the data input stream used to read from the socket. */
    private DataInputStream in;
    
	/* The thread related variables. */
	private ConnectionLock connectionLock = new ConnectionLock();
	private ClientWriteTCP writeThread;
	
	/*The socket variable we shall use to connect to the server.*/
	private Socket clientSocket = null;
    
    
	public static void main(String args[]) {
		
		if (args.length != 2) {
		    System.out.println("java -jar [fileName] [firstServerIpAddress] [secondServerIpAddress]");
		    System.exit(-1);
		}
		
		/* Reads the properties. */
		Constants.readProperties("properties.conf");
		
		/* Initializes the application. */
		new TCPClient(args[0], args[1]);
    }
		
	@SuppressWarnings("unchecked")
	public TCPClient(String ipServerOne, String ipServerTwo){
		
		/*The variables related to the reconnection.*/
		int retries = 0; //The numbers of times we reconnected already to a given port.
		int retrying = 0; //Tests if we are retrying for the first or second time.
		
		/* Variables used for the login authentication. */
		boolean loggedIn = false;
		
		/* The variables related to the server ports available. */
		int []serverPorts = new int[2]; //The array with the two different ports.
		String []serverIps = new String[2]; //The array with the two different ports.
		int serverPos = 0; //The position array, which corresponds to active port.
		int noServerPorts = serverPorts.length; //Total number of possible servers ports.
		/* Places the two ports and IP's in the respective arrays. */
		serverPorts[0] = Constants.FIRST_TCP_SERVER_PORT;
		serverPorts[1] = Constants.SECOND_TCP_SERVER_PORT;
		serverIps[0] = ipServerOne;
		serverIps[1] = ipServerTwo;
		
		/* Variables to deal with the loggin. */
		String [] stringSplitted;
		String command = "";
		String serverAnswer;
		boolean error = false;
		
		writeThread =  new ClientWriteTCP(connectionLock);
		writeThread.setReadThread(this);
		
		/* Displays an initial message, to ensure the user the application hasn't frozen. */
		System.out.println("Welcome to the BetAndUin! Please wait while we try to connect to our server.\n");
		
		/* Five attempts to reconnect the connection. */
		while (retries < Constants.NO_RETRIES){
			try {			
				/* We haven't retried yet, so, it's useless to sleep for WAITING_TIME milliseconds. */
				if (retrying > 0){
				    try {
						Thread.sleep(Constants.CLIENT_WAITING_TIME);
					} catch (InterruptedException e) {
						if (Constants.DEBUGGING_CLIENT)
							System.out.println("TCPClient: This thread was interrupted while sleeping.\n");
						System.exit(0);
					}
				}
				/* Connects to the server. */
				clientSocket = new Socket(serverIps[serverPos], serverPorts[serverPos]);
				
				if (Constants.DEBUGGING_CLIENT){
					System.out.println("TCPClient: SOCKET = " + clientSocket);
				}
				
				if (!firstConnection){
					System.out.println("Connected to server in port " + serverPorts[serverPos] + ".");
					firstConnection = true;
				}
				else{
					System.out.println("Has successfully reconnected to server, now in port " + serverPorts[serverPos] + ".");
				}
				
				/* Resets the right socket connection. */
			    writeThread.setSocket(clientSocket);
				
				/* Initializes the input stream. */
				in = new DataInputStream(clientSocket.getInputStream());
				
				/* Login authentication. */
				while(!loggedIn){
					/* When the server goes down, the client keep the data related to a successful login
					 * When the server is up again, the client application directly sends that information
					 * so the end user won't have to reinsert them once again.
					 */
					if(!(command.equals("")) && !error){
						if (Constants.DEBUGGING_CLIENT){
							System.out.printf("TCPClient: We already have some data saved (%s).\n", command);
						}
						writeThread.out.writeUTF(command);
						serverAnswer = in.readUTF();
					}
					/* The information needed for a valid login hasn't been inserted yet. */
					else {
						if (Constants.DEBUGGING_CLIENT){
							System.out.println("TCPClient: We don't have any login saved.");
						}
						
						/* Prints on the screen the information necessary to take the first step
						 * into making the login or register in the system.
						 */
			        	System.out.println("\nTo log in: login [user] [pass]\n" +
		    			"To register in: register [user] [pass] [email]");	
						
						error=false;
			        	command = writeThread.reader.readLine();
			        	stringSplitted = command.split(" ");
			        	
			        	/* If the client is registering and hasn't inserted four keywords,
			        	 * or if the client is logging and hasn't inserted three keywords
			        	 * or if he has entered and unknown command, we try again.
			        	 */
			        	if(!((stringSplitted.length == 4 && stringSplitted[0].equals("register")) 
			    				|| (stringSplitted.length == 3 && stringSplitted[0].equals("login")))){
			        		System.out.println("Unknown command.");
			        		error=true;
			        		continue;
			    		}
			        	
			        	/* Write into the socket connected to the server. */
			        	writeThread.out.writeUTF(command);
			        	/* Now, it waits for the answer from the server. */
			        	serverAnswer = in.readUTF();
					}
					
					String splittedAnswer = serverAnswer.split(" ")[0];
					
					/* The server informs the client that there was some kind of error in the validation
					 * process. */
		        	if (!serverAnswer.equals("log successful") && !splittedAnswer.equals("BetAndUinChat:")){
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
		        		
		        		/* Resets the variables. */
		        		serverAnswer = "";
		        		error = true;
		        	}
		        	/* The login has been validated, so the client can now proceed. */
		        	else if (serverAnswer.equals("log successful") || splittedAnswer.equals("BetAndUinChat:")){
		        		loggedIn = true;
		        		writeThread.setUserName(command.split(" ")[1]);
		        		System.out.println("You are now logged in!");
		        	}
				}
				
				/* Sends the undelivered messages to the server. */
				/* The user name is in the first field of the command. */
				writeThread.msgBuffer = (Vector<String>) writeThread.readObjectFromFile(command.split(" ")[1]);
				
				/* There was some kind of trouble while reading from the file, so we simply
				 * create a new message buffer.
				 */
				if(writeThread.msgBuffer == null){
					writeThread.msgBuffer = new Vector<String>();
				}
				else {
					/* While there are messages to read, the thread keeps sending old messages
					 * to the server.
					 */
					while(!writeThread.msgBuffer.isEmpty()){
						writeThread.out.writeUTF(writeThread.msgBuffer.firstElement());
						writeThread.msgBuffer.remove(0);
					}
					
					 /* Cleans the file. */
					writeThread.saveObjectToFile(command.split(" ")[1], null);
				}
	        	
				
				/* Warns the writing thread that it start working. */
		    	synchronized(connectionLock){
		    		connectionLock.setConnectionDown(false);
		    		connectionLock.notify();
		    	}
		    	
			    /* Starts reading from the socket.
			     * If we ever return from this method, it means that we had problems
			     * with the socket connection.
			     */
			    clientRead();

			    /* Resets the counters, the lock flags and the login variables. */
				retries = 0;
				retrying = 0;
				loggedIn = false;
				error = false;
			    
			/* The list of possible exceptions to be handled. */
			} catch (UnknownHostException e) {
				if (Constants.DEBUGGING_CLIENT){
					System.out.println("TCPClient: Sock:" + e.getMessage());
				}
			} catch (EOFException e) {
				if (Constants.DEBUGGING_CLIENT){
					System.out.println("TCPClient: EOF:" + e.getMessage());
				}
			} catch (IOException e) {
				if (Constants.DEBUGGING_CLIENT){
					System.out.println("TCPClient: IO:" + e.getMessage());
				}
			    retries++;
			    /* In this case, it's the first connection, and the server is already down.
			     * So, we will pass immediately to the next one and retry this one later.
			     */
			    if (retries == 1 && retrying == 0){
			    	serverPos = (++serverPos)%noServerPorts;
			    	System.out.println("Trying to connect to server in port " + serverPorts[serverPos] + ".");
			    }
			    /* We have retried the connection at least once.
			     * Consequently, the thread shall wait WAITING_TIME milliseconds 
			     * when restarts the while cycle.
			     */
			    else if (retrying == 0){
			    	retrying = 1;
			    	System.out.println("Trying to connect to server in port " + serverPorts[serverPos] + ".");
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
			    if (clientSocket != null)
				try {
					clientSocket.close();
				} catch (IOException e) {
					if (Constants.DEBUGGING_CLIENT)
						System.out.println("TCPClient: Close:" + e.getMessage());
				}
			}
		}
		
		System.out.println("Exited");
		System.exit(0);
    }
    
    /* Method invoked to read from the socket and print, if required, on the screen. */
    public void clientRead(){
    	
        try{
        	
            while(true){
            	
                /* Reads the input from the user. */
                String data = in.readUTF();
                /* This operation was asked by the internals of the system
                 * and not directly by the user.
                 */
                if (!isToPrint){
                	int credits;
                	try{
                		credits = Integer.parseInt(data);
                		isToPrint = true;
                		writeThread.setUserCredtis(credits);
                    	writeThread.interrupt();
                	}catch(Exception e){
                		/* This isn't the number of credits. */
                		System.out.println(data);
                	}
                
                }
                else{
                	System.out.println(data);
	            	System.out.print(" >>> ");
                }
                
            }
        }catch(EOFException e){
        	if (Constants.DEBUGGING_CLIENT){
        		System.out.println("TCPClient: EOF in ClientReadTCP:" + e);
        	}
        }catch(IOException e){
        	if (Constants.DEBUGGING_CLIENT){
        		System.out.println("TCPClient: IO in ClientReadTCP:" + e);
        	}
        	System.out.println("The server is down. Please wait while we try to reconnect...");
        	return;
        }
    }
    
    public void setIsToPrint(boolean value){
    	isToPrint = value;
    }
}
