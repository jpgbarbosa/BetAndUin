package clientRMI;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;
import java.util.Vector;

import common.ConnectionLock;
import common.Constants;


import server.ClientOperations;


public class RMIWriter extends Thread{
	/*Set to true if you want the program to display debugging messages.*/
	private Boolean debugging = true;
	
	/* A reference to the thread that holds the main server, to which this RMIWriter
	 * is bounded. */
	private ClientOperations server;
	
	/* The connectionLock and msgBuffer so we can save 'send' messages when the server
	 * is down.
	 */
	private ConnectionLock connectionLock;
	Vector<String> msgBuffer;

	/* Variables related to the input and analysis of the messages. */
	private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	private String userInput, serverAnswer, username;
	private String [] stringSplitted;
	
	RMIWriter(ConnectionLock connectionLock){
		this.connectionLock = connectionLock;
		msgBuffer = new Vector<String>();
		this.start();
	}
	
	public void run(){
		
		/* Waits for the connection to be up before engaging in any activity. */
    	synchronized(connectionLock){
    		while (connectionLock.isConnectionDown()){
    			try {
    				connectionLock.wait();
				} catch (InterruptedException e) {
					if (debugging){
						System.out.println("ClientWriteRMI Thread interrupted.");
					}
				}
    		}
    	}
		
    	/* Starts to function. */
        while(true){
        	try{ 
        		/* Reads the input from the user and divides its keywords. */
    	    	stringSplitted = null;
    	    	userInput =  reader.readLine();
    	    	stringSplitted = userInput.split(" ");
    	    	
            	synchronized(connectionLock){
            		/* The connection is down, so we may need to save the message. */
	            	if(connectionLock.isConnectionDown()){
	            		/* This is a valid message to be saved. */
	            		if(stringSplitted.length >= 3 && stringSplitted[0].equals("send")){
	            			msgBuffer.add(userInput);
		            		saveObjectToFile(username, msgBuffer);
		            		System.out.println("The server is down, so we will save the message to send later.");
		            		System.out.print(">>> ");
	            		}
	            		/* We can't execute this operation, because it is not a send. */
	            		else{
	            			System.out.println("The connection is down and this operation couldn't be completed.");
	            			System.out.print(">>> ");
	            		}
	            	}
	            	/* The connection is up, so we can easily send a message. */
	            	else if(!connectionLock.isConnectionDown()){
						serverAnswer = parseFunction(username, stringSplitted, userInput, server, reader);
						System.out.println(serverAnswer);
						System.out.print("\n>>> ");
	            	}
            	}
            }catch(RemoteException e){
            	/* If we ever entered here, it means that it was this thread that noticed
            	 * for the first time that the connection was down.
            	 * Consequently, we have to save the messages that are to be saved, change
            	 * the status of the connection and warn all the other threads interested
            	 * about this changes.
            	 */
            	
            	/* This is a valid message to be saved. */
        		if(stringSplitted.length >= 3 && stringSplitted[0].equals("send")){
        			msgBuffer.add(userInput);
            		saveObjectToFile(username, msgBuffer);
            		System.out.println("The server is down, so we will save the message to send later.");
            		System.out.print(">>> ");
        		}
        		/* We can't execute this operation, because it is not a send. */
        		else{
        			System.out.println("The connection is down and this operation couldn't be completed.");
        			System.out.print(">>> ");
        		}
        		
        		/* Updates the state of the connection. */
        		synchronized(connectionLock){
        			connectionLock.setConnectionDown(true);
            		connectionLock.notify();
        		}
	        } catch (IOException e) {
	        	if (debugging)
	        		System.out.println("IOException in RMIWriter: " + e.getMessage());
	        	System.exit(-1);
			}catch (Exception e) {
				if (debugging)
					System.out.println("Exception in RMIWriter: " + e.getMessage());
	        	
				System.exit(-1);
			}
        }
	}
	
   protected String parseFunction(String user, String [] stringSplitted, String input, ClientOperations server, BufferedReader reader) throws RemoteException{
    	/* The answer from the server to the client. */
    	String answer = "";
    	
        /* The client has sent two keywords and the first is "show".*/
        if(stringSplitted.length == 2 && stringSplitted[0].equals("show")){        	
        	if(stringSplitted[1].equals("matches")){ //show all current matches
        		answer = server.clientShowMatches();
        	}
        	else if(stringSplitted[1].equals("credits")){ //show user's credits
        		answer = server.clientShowCredits(user);
        	}
        	else if(stringSplitted[1].equals("users")){ //show all active users
        		answer = server.clientShowUsers();
        	}
        	else if(stringSplitted[1].equals("menu")){//show the main menu.
        		answer = server.clientShowMenu();
        	}
        	else{
        		answer = "Unknow Command";
        	}
        }
        
        /* The user is attempting to send a message. */
        else if(stringSplitted.length >= 3 && stringSplitted[0].equals("send")){
        	if(stringSplitted[1].equals("all")){ //send a message to all users
        		/* We have to use again the input because the user may send the message with
        		 * white spaces.
        		 * 'send all ' has 9 characters. So, the message is from there till the end of
        		 * input string.
        		 */
        		answer = server.clientSendMsgAll(user, input.substring(9));
        	}
        	else if (stringSplitted[1].equals(user)){
        		answer = "What's the point of sending messages to yourself?";
        	}
        	/* We are sending a message to a user. */
        	else{
        		/* 'send ' has 5 characters. Then, we have to sum the size of the receiver name
        		 * as well as a white space that separates this name from the message.
        		 */
        		answer = server.clientSendMsgUser(user, stringSplitted[1],input.substring(6 + stringSplitted[1].length()));
        	}
        }
        
        /* The user is attempting a bet. */
        else if(stringSplitted.length == 4 && stringSplitted[0].equals("bet")){
        	/* Variables to save the values inserted by the client. */
        	String resultBet;
        	int gameNumber,credits;
        	
        	try {
        		gameNumber = Integer.parseInt(stringSplitted[1]);
        		resultBet = stringSplitted[2];
        	    credits = Integer.parseInt(stringSplitted[3]);
        	    
        	} catch(NumberFormatException nFE) {
        		/* The user hasn't inserted a number for one of the required arguments. */
        		System.out.println("Not an integer.");
        		
        		answer = "Invalid game number or amount of credits!";
        	    return answer;
        	}
        	
        	/* The user has made a bet of 0 credits. */
        	if (credits == 0){
        		return "Are you kidding?! You have bet no credits!";
        	}
        	
        	answer = server.clientMakeBet(user,gameNumber, resultBet, credits);
        }
        
        /* The number of arguments by the user to make a bet is invalid. */
        else if(stringSplitted.length > 0 && stringSplitted[0].equals("bet")){
        	answer =  "Wrong number of arguments: bet [game number] [bet] [credits]";
        }
        
        /* The user wants to reset the number of credits on the account. */
        else if (input.equals("reset")){
        	/* First, we verify if the client has more or less than the default value of credits,
        	 * make sure he/she won't lose credits accidentally.
        	 */
    		int userCredits = Integer.parseInt(server.clientShowCredits(user));

    		if (userCredits > Constants.DEFAULT_CREDITS){
    			String finalAnswer = "";
    			System.out.printf("In this moment, you have %d, which means you are going to lose %d credits.\n" +
    					"Are you sure you want to continue with the process (Y/N)?\n", userCredits, userCredits - Constants.DEFAULT_CREDITS);
    			
    			/* If the user has more credits than the number of default credits, we have
    			 * to ask the user if he/she really wants to lower the number of credits
    			 * on his/her account. */
    			do{
    				try{
    					finalAnswer = reader.readLine().toUpperCase();
    				}catch (Exception e){
    					return "";
    				}
    			}
    			while (!finalAnswer.equals("Y") && !finalAnswer.equals("N"));
    			
    			/* Verifies the answer given by the user. */
    			if (finalAnswer.equals("Y")){
    				answer = server.clientResetCredits(user);
    			}
    			else{
    				answer = "Operation cancelled. You still have " + userCredits+ " credits.\n";
    			}
        	}
    		/* If the client has less credits than the number of default credits, we can reset the
    		 * number without bother asking.
    		 */
    		else{
    			answer = server.clientResetCredits(user);
    		}
        }
        /* We are leaving the program. */
        else if(stringSplitted.length == 1 && stringSplitted[0].equals("exit")){
        	server.clientLeave(user);
        	System.out.println("Thank you for using the BetAndUin service!\n"
    				+ "Have a nice day!");
    		System.exit(0);
        }
        /* The command entered by the user is unknown to the system. */
        else {
        	answer = "Unknown command";
        }

		return answer;
    }
   
	synchronized protected void saveObjectToFile(String user, Object obj){
		ObjectOutputStream oS;
		/* Creates a name for a specific file for a client. */
		String filename = user + ".bin";
		
		try {
			oS = new ObjectOutputStream(new FileOutputStream(filename));
			oS.writeObject(obj);
		} catch (FileNotFoundException e) {
			if(debugging)
				System.out.println("The " + filename + " file couldn't be found...");
		} catch (IOException e) {
			if(debugging)
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
	
	public void setUserName(String user){
		username = user;
	}
	
	public void setServer(ClientOperations s){
		server = s;
	}
}
