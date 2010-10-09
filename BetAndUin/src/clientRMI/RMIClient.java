package clientRMI;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
//import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.*;

import server.ClientOperations;


public class RMIClient extends UnicastRemoteObject implements ServerOperations{

	private static final long serialVersionUID = 1L;

	RMIClient() throws RemoteException {
		super();
	}
	
	public static void main(String args[]) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));;
		String [] stringSplitted = null;
		String username="",password="";
		String serverAnswer = "";
				
		boolean loggedIn = false;
		ClientOperations server = null;
		
		try {
			RMIClient client = new RMIClient();
			server = (ClientOperations) Naming.lookup("rmi://localhost:12000/BetAndUinServer");
		
			System.out.println(server.clientShowMenu());
			while(true){
	
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
			        	} else {
			        		System.out.println("You're now logged in!");
			        		loggedIn = true;
			        	}
			        	
					}
					/* The client is logged in already. */
					else{
						serverAnswer = server.clientLogin(username, password, (ServerOperations) client);
						
					}
				} //while(!loggedIn)
				
				serverAnswer = client.parseFunction(username, reader.readLine(), server);
				System.out.println(serverAnswer);
				
			} //while(true)
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void printUserMessage(String msg) throws java.rmi.RemoteException{
		System.out.println();
	}
	
    public String parseFunction(String user, String input, ClientOperations server) throws RemoteException{
    	/* The answer from the server to the client. */
    	String answer = "";
    	
    	/* Splits the input. */
    	String [] stringSplitted = input.split(" ");
        
        /* The client has sent two keywords and the first is "show".*/
        if(stringSplitted.length == 2 && stringSplitted[0].equals("show")){        	
        	if(stringSplitted[1].equals("matches")){ //show all current matches
        		answer = server.clientShowMatches();
        	}
        	else if(stringSplitted[1].equals("credits")){ //show user's credits
        		answer = server.clientResetCredits(user);
        	}
        	else if(stringSplitted[1].equals("users")){ //show all active users
        		answer = server.clientShowUsers();
        	}
        	else{
        		answer = "Unknow Command";
        	}
        }
        else if(stringSplitted.length == 3 && stringSplitted[0].equals("send")){
        	if(stringSplitted[1].equals("all")){ //send a message to all users
        		answer = server.clientSendMsgAll(user, stringSplitted[2]);
        	}
        	/* We are sending a message to a user. */
        	else{
        		answer = server.clientSendMsgUser(user, stringSplitted[1], stringSplitted[2]);
        	}
        }
        else if(input.equals("reset")){ //resets user's credits to 100Cr
        	answer = server.clientResetCredits(user);
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
        	
        	server.clientMakeBet(user,gameNumber, resultBet, credits);
        }
        else if(stringSplitted.length > 0 && stringSplitted[0].equals("bet")){
        	answer =  "Wrong number of arguments: bet [game number] [bet] [credits]";
        }
        else {
        	answer = "Unknown command";
        }

		return answer;
    }
}
