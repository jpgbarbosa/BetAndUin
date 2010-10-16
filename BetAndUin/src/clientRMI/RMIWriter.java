package clientRMI;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;
import java.util.Vector;

import constants.Constants;

import server.ClientOperations;
import server.Server;

import clientTCP.ConnectionLock;

public class RMIWriter extends Thread{

	Server server;
	
	ConnectionLock connectionLock;
	Vector<String> msgBuffer;
	
	BufferedReader reader;
	
	boolean debugging = true;
	
	String userInput, serverAnswer, username;
	String [] stringSplitted;
	
	RMIWriter(ConnectionLock connectionLock){
		this.connectionLock = connectionLock;
		msgBuffer = new Vector<String>();
		this.start();
	}
	
	public void run(){
		
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
		
        while(true){
        	try{ 	
    	    	stringSplitted = null;
    	    	userInput =  reader.readLine();
    	    	
    	    	stringSplitted = userInput.split(" ");
            	
            	synchronized(connectionLock){
	            	if(connectionLock.isConnectionDown() && stringSplitted[0].equals("send")){
	            		msgBuffer.add(userInput);
	            		saveObjectToFile("buffer.bin", msgBuffer);
	            	} else if(!connectionLock.isConnectionDown()){
						System.out.println("\n>> ");
						serverAnswer = parseFunction(username, stringSplitted, userInput, server, reader);
						System.out.println(serverAnswer);
	            	}
            	}
            }catch(RemoteException e){
        		msgBuffer.add(userInput);
        		saveObjectToFile("buffer.bin", msgBuffer);
            	/*Se estoirou, significa que temos de guardar este ultimo comando
            	* visto que só estoira qd efectuamos o comando, certo?
            	*/
            	connectionLock.setConnectionDown(true);
            	connectionLock.notify();
	        } catch (IOException e) {
				e.printStackTrace();
			}
        }
	}
	
   public String parseFunction(String user, String [] stringSplitted, String input, ClientOperations server, BufferedReader reader) throws RemoteException{
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
        else if(stringSplitted.length > 0 && stringSplitted[0].equals("bet")){
        	answer =  "Wrong number of arguments: bet [game number] [bet] [credits]";
        }
        else if (input.equals("reset")){
        	/* First, we verify if the client has more or less than the default value of credits,
        	 * make sure he/she won't lose credits accidentally.
        	 */
    		int userCredits = Integer.parseInt(server.clientShowCredits(user));

    		if (userCredits > Constants.DEFAULT_CREDITS){
    			String finalAnswer = "";
    			System.out.printf("In this moment, you have %d, which means you are going to lose %d credits.\n" +
    					"Are you sure you want to continue with the process (Y/N)?\n", userCredits, userCredits - Constants.DEFAULT_CREDITS);
    			do{
    				try{
    					finalAnswer = reader.readLine().toUpperCase();
    				}catch (Exception e){
    					return "";
    				}
    			}
    			while (!finalAnswer.equals("Y") && !finalAnswer.equals("N"));
    			
    			if (finalAnswer.equals("Y")){
    				answer = server.clientResetCredits(user);
    			}
    			else{
    				answer = "Operation cancelled. You still have " + userCredits+ " credits.\n";
    			}
        	}
    		else{
    			answer = server.clientResetCredits(user);
    		}
        }
        else if(stringSplitted.length == 1 && stringSplitted[0].equals("exit")){
        	server.clientLeave(user);
        	System.out.println("Thank you for using the BetAndUin service!\n"
    				+ "Have a nice day!");
    		System.exit(0);
        }
        else {
        	answer = "Unknown command";
        }

		return answer;
    }
   
	synchronized public void saveObjectToFile(String filename, Object obj){
		ObjectOutputStream oS;
		
		try {
			oS = new ObjectOutputStream(new FileOutputStream(filename));
			oS.writeObject(obj);
		} catch (FileNotFoundException e) {
			System.out.println("The clientsDatabase.bin file couldn't be found...");
		} catch (IOException e) {
			System.out.println("IO in saveToFile (ClientsStorage): " + e);
		}
	}
	
	/* The reading method for an object. This method can only be used by the class. */
	synchronized public Object readObjectFromFile(String filename){
		ObjectInputStream iS;
		
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
		
	protected void setUserName(String username){
		this.username = username;
	}
	
	protected void setServer(Server server){
		this.server = server;
	}
}
