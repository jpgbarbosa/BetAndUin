package clientTCP;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Vector;

import constants.Constants;



public class ClientWriteTCP extends Thread {
	/*Set to true if you want the program to display debugging messages.*/
	private boolean debugging = true;
	
	//This thread will be responsible for handling problems with the link to the server.
	private String username;
    protected DataOutputStream out;
    private Socket clientSocket;
    private String userInput;
    private  ConnectionLock connectionLock;
    private ClientReadTCP readThread;
    private int userCredits;
    
    protected Vector<String> msgBuffer;
    
    protected BufferedReader reader;
    
    public ClientWriteTCP (ConnectionLock lock) {
    	connectionLock = lock;
    	userCredits = 0;
    	msgBuffer = new Vector<String>();
        this.start();
    }
    
    //=============================
    public void run(){
    	
    	/* Waits for the connection to be up before engaging in any activity. */
    	synchronized(connectionLock){
    		while (connectionLock.isConnectionDown()){
    			try {
    				connectionLock.wait();
				} catch (InterruptedException e) {
					if (debugging){
						System.out.println("ClientWriteTCP Thread interrupted.");
					}
				}
    		}
    	}
    	
    	/* Shows the main menu. */
    	try {
			out.writeUTF("show menu");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
    	while (true){
	        try{
	            while(true){
	            	userInput = reader.readLine();
	            	
	            	/* The user is going to reset the number of credits.
	            	 * In the case we have more than 100, we have to make sure
	            	 * the client noticed this and willingly making an action
	            	 * that will make him lost some credits.
	            	 */
	            	synchronized(connectionLock){
	            		/* The connection is up and running. */
	            		if (!connectionLock.isConnectionDown()){
			            	try{
				            	if (userInput.equals("reset")){
				            		readThread.setIsToPrint(false);
				            		out.writeUTF("show credits");
				            		try{
				            			synchronized(this){
				            				this.wait();
				            			}
				            		}catch (InterruptedException e){
				            			/* Continues the work. */
				            		}
				            		
				            		if (userCredits > Constants.DEFAULT_CREDITS){
				            			String finalAnswer = "";
				            			System.out.printf("In this moment, you have %d, which means you are going to lose %d credits.\n" +
				            					"Are you sure you want to continue with the process (Y/N)?\n", userCredits, userCredits - Constants.DEFAULT_CREDITS);
				            			do{
				            				try{
				            					finalAnswer = reader.readLine().toUpperCase();
				            				}catch (Exception e){
				            					return;
				            				}
				            			}
				            			while (!finalAnswer.equals("Y") && !finalAnswer.equals("N"));
				            			
				            			if (finalAnswer.equals("Y")){
				            				out.writeUTF("reset");
				            			}
				            			else{
				            				System.out.printf("Operation cancelled. You still have %d credits.\n", userCredits);
				            			}
				            		}
				            		else{
				            			out.writeUTF("reset");
				            		}
				            	}
				            	/* The user has selected the option to exit the program. */
				            	else if (userInput.equals("exit")){
				            		System.out.println("Thank you for using the BetAndUin serivce!\n"
				            				+ "Have a nice day!");
				            		System.exit(0);
				            	}
				            	else{
				            		/* We verify the validity of the commands' on the client side
				            		 * in order to avoid unnecessary transmission and don't push
				            		 * too much the server with this checking.
				            		 */
				            		//TODO: Check if we are going to implement this last comment or
				            		//		not.
				            		out.writeUTF(userInput);
				            	}
				            }catch(Exception e){
					        	return;
					        }
	            		}
	            		/* The connection down and we have to analyze the message. */
	            		else{
	            	    	String [] stringSplitted = userInput.split(" ");
	            	    	
	            	    	if (stringSplitted.length >= 3 && stringSplitted[0].equals("send")){
	            	    		msgBuffer.add(userInput);
			            		saveObjectToFile(username, msgBuffer);
			            		System.out.println("The server is down, so we will save the message to send later.");
			            		System.out.print(">>> ");
	            	    	}
	            	    	else{
	            	    		System.out.println("The connection is down and this operation couldn't be completed.");
		            			System.out.print(">>> ");
	            	    	}
	            			
	            			
	            		}
	            	}
	            }
	        }catch(EOFException e){
	        	if (debugging){
	        		System.out.println("ClientWriteTCP EOF:" + e);
				}
	        	
	        }catch(IOException e){
	        	if (debugging){
	        		System.out.println("ClientWriteTCP IO:" + e);
				}
	        	
	        }
    	}
    }
    
	synchronized protected void saveObjectToFile(String user, Object obj){
		ObjectOutputStream oS;
		/* Creates a name for a specific file for a client. */
		String filename = user + ".bin";
		
		try {
			oS = new ObjectOutputStream(new FileOutputStream(filename));
			oS.writeObject(obj);
		} catch (FileNotFoundException e) {
			System.out.println("The " + filename + " file couldn't be found...");
		} catch (IOException e) {
			System.out.println("IO in saveToFile (ClientsStorage): " + e);
		}
	}
	
	/* The reading method for an object. This method can only be used by the class. */
	synchronized protected Object readObjectFromFile(String user){
		ObjectInputStream iS;
		String filename = user + ".bin";
		
		/* We now read the list of clients. */
		try {
			iS = new ObjectInputStream(new FileInputStream(filename));
			return iS.readObject();
		} catch (FileNotFoundException e) {
			System.out.println("The " + filename + " file couldn't be found...");
			return null;
		} catch (ClassNotFoundException e) {
			System.out.println("ClassNotFound in readFromFile (ClientsStorage): " + e);
			return null;
		}catch (IOException e) {
			System.out.println("IO in readFromFile (ClientsStorage): " + e);
			return null;
		}
	}
    
    protected void setSocket(Socket s){
    	clientSocket = s;
    	try{
    		out = new DataOutputStream(clientSocket.getOutputStream());
    	    reader = new BufferedReader(new InputStreamReader(System.in));
        }catch(IOException e){System.out.println("Connection:" + e.getMessage());}
    }
    
    public void setReadThread(ClientReadTCP thread){
    	readThread = thread;
    }
    
    public void setUserCredtis(int credits){
    	userCredits = credits;
    }
    
    public void setUserName(String user){
    	username = user;
    }
	
}
