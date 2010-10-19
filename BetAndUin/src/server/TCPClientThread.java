package server;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.rmi.RemoteException;

import constants.Constants;

/* Thread used to take care of each communication channel between the active server and a given client. */
class TCPClientThread extends Thread {
	/*Set to true if you want the program to display debugging messages.*/
	private Boolean debugging = false;
	
	/* The betScheduler so we can send the matches' information back to the client. */
	private BetScheduler betScheduler; 
	/* A pointer to the information block related to this client. */
	private ClientInfo clientInfo;
	
	/* This two variables keep the values inserted by the user, so we can use it later. */
	private String username, password;
	
	private DataInputStream in;
	private Socket clientSocket;
	private ActiveClients activeClients;
	private GlobalDataBase database;
	private Server server;

    public TCPClientThread (Server server, Socket aClientSocket, ActiveClients activeClients, BetScheduler betScheduler, GlobalDataBase database) {
        this.betScheduler=betScheduler;
        this.database = database;
        this.server = server;
        try{
            clientSocket = aClientSocket;
            in = new DataInputStream(clientSocket.getInputStream());
            this.activeClients = activeClients;
            this.start();
        }catch(IOException e){System.out.println("Connection:" + e.getMessage());}
    }

    //=============================
    public void run(){
    	boolean loggedIn = false;
    	
        try{
        	/* Performs login authentication. */
        	while(!loggedIn){
        		String [] stringSplitted;

                /* Reads and splits the input. */
                stringSplitted = in.readUTF().split(" ");
                	
                if(stringSplitted.length == 4 && stringSplitted[0].equals("register")){
                	username = stringSplitted[1];
                	password = stringSplitted[2];
                	String mail = stringSplitted[3];
                	
                	/* We don't permit that a user registers under the name of 'all',
                	 * because it would interfere with the analysis of the commands
                	 * sent to the server.
                	 */
                	if (username.equals("all")){
                		activeClients.sendMessageBySocket("username all",clientSocket);
                	}
                	/* This username hasn't been found in the database, so we can add this new client. */
                	else if (database.findClient(username) == null){
                		/* Creates the register for this new client and adds it to the database. */
                		 clientInfo = database.addClient(username, password, mail);
                		 
                		/* Registers in the client as an active client and informs the success of the
                		 * operation.
                		 */
                		activeClients.addClient(clientInfo.getUsername(), clientSocket, null);
                		activeClients.sendMessageBySocket("log successful",clientSocket);
                		loggedIn = true;
                	}
                	/* This username is already being used. */
                	else{
                		/* Resets the variables and writes to the client. */
                		username = "";
                		password = "";
                		activeClients.sendMessageBySocket("log taken",clientSocket);
                	}
                	
                } else if(stringSplitted.length == 3 && stringSplitted[0].equals("login")){
                	ClientInfo client;
                	username = stringSplitted[1];
                	password = stringSplitted[2];
                	
                	client = database.findClient(username);
                	/* This username hasn't been found on the database. */
                	if (client == null){
                		activeClients.sendMessageBySocket("log error",clientSocket);
                	}
                	/* This username has been found on the database. Let's check if the password matches
                	 * with it.
                	 */
                	else{
		                if(password.equals(client.getPassword())){
		                	/* However, the user was already validated in some other machine. */
		                	if (activeClients.isClientLoggedIn(username) == true){
		                		activeClients.sendMessageBySocket("log repeated",clientSocket);
		                	}
		                	/* The validation process can be concluded. */
		                	else{
		                		loggedIn=true;
		                		clientInfo = client;
		                		activeClients.addClient(username, clientSocket, null);
		                		activeClients.sendMessageUser("log successful",username);
		                	}
		                }
		                else {
	                		activeClients.sendMessageBySocket("log error",clientSocket);
	                	}
                	}
                }
        	}
            while(true){
                /* Now, the server can communicate with the client, receiving the requests
                 * and sending back the respective information.
                 */
                String clientInput = in.readUTF();
                /* Interprets the commands sent by the client and performs the respective
                 * action.
                 */
                String data = parseFunction(clientInput);
                /* Replies to the client. */
                /* We only reply if data != "". If we received "", it means we were sending a message
                 * to all the users.
                 */
                if (!data.equals("")){
                	activeClients.sendMessageBySocket(data, clientSocket);
                }
            }
        }catch(EOFException e){
        	/* The client is leaving. Consequently, we have to remove it from the list
        	 * of active clients.
        	 */
        	activeClients.removeClient(username);
        	if (debugging){
        		System.out.println("ConnectionChat EOF:" + e);
        	}
        }catch(IOException e){
        	/* The client is leaving. Consequently, we have to remove it from the list
        	 * of active clients.
        	 */
        	activeClients.removeClient(username);
        	if (debugging){
        		System.out.println("ConnectionChat IO:" + e);
        	}
        }
    }
    
    /* The parsing function. Given a request from a client, this thread must recognized the commands
     * and get the right information, so the thread can send it to the client.
     */
    public String parseFunction(String input){
    	/* The answer from the server to the client. */
    	String answer = "";
    	
    	/* Splits the input. */
    	String [] stringSplitted = input.split(" ");
        
        /* The client has sent two keywords and the first is "show".*/
        if(stringSplitted.length == 2 && stringSplitted[0].equals("show")){        	
        	if(stringSplitted[1].equals("matches")){ //show all current matches
        		answer = betScheduler.getMatches();
        	}
        	else if(stringSplitted[1].equals("credits")){ //show user's credits
        		answer = "" + clientInfo.getCredits();
        	}
        	else if(stringSplitted[1].equals("users")){ //show all active users
        		answer = activeClients.getUsersList();
        	}
        	else if(stringSplitted[1].equals("menu")){   	
            	try {
					answer = server.clientShowMenu();
				} catch (RemoteException e) {
					System.out.println("Exception: " + e);
				}
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
        		activeClients.sendMessageAll(clientInfo.getUsername() + " says to everyone: " + input.substring(9), clientSocket, null);
        		answer = "";
        	}
        	/* We are sending a message to a user. */
        	else{
        		/* This client is online. */
	        	if(activeClients.isClientLoggedIn(stringSplitted[1]) == true){
	        		/* Checks if client isn't sending a message to himself/herself. */
	        		if (stringSplitted[1].equals(clientInfo.getUsername())){
	        			/* Alternative: Are you feeling alone? */
	        			activeClients.sendMessageUser("What's the point of sending messages to yourself?", stringSplitted[1]);
	        		}
	        		else{
	        			/* 'send ' has 5 characters. Then, we have to sum the size of the receiver name
	            		 * as well as a white space that separates this name from the message.
	            		 */
	        			activeClients.sendMessageUser(clientInfo.getUsername() + " says: " + input.substring(6 + stringSplitted[1].length()), stringSplitted[1]);
	        		}
	        		answer = "";
	        	}
	        	else if(database.findClient(stringSplitted[1]) != null){
	        		answer = stringSplitted[1] + " is offline at the moment.";
	        	}
	        	else{
	        		answer = "Username not registered.";
	        	}
        	}
        }
        else if(input.equals("reset")){ //resets user's credits to 100Cr
        	System.out.println("Here");
        	clientInfo.setCredits(Constants.DEFAULT_CREDITS);
        	answer = "Your credits were reseted to " + clientInfo.getCredits() + "Cr";
        	database.saveObjectToFile("clientsDatabase.bin", database.getClientsDatabase());
        }
        else if(stringSplitted.length == 4 && stringSplitted[0].equals("bet")){
        	/* Variables to save the values inserted by the client. */
        	String resultBet;
        	int gameNumber,credits;
        	
        	try {
        		gameNumber = Integer.parseInt(stringSplitted[1]);
        		resultBet = stringSplitted[2];
        	    credits = Integer.parseInt(stringSplitted[3]);
        	    
        	}
        	catch(NumberFormatException nFE) {
        		/* The user hasn't inserted a number for one of the required arguments. */
        		if (debugging){
        			System.out.println("Not an integer.");
        		}
        		
        	    answer = "Invalid game number or amount of credits!";
        	    return answer;
        	}
        	
        	/* If the client is betting more credits than he/she has on his/her account,
        	 * we cannot conclude the bet. Consequently, we have to send a message
        	 * warning the user about it.
        	 */
        	if (clientInfo.getCredits() < credits){
        		answer = "You don't have enough credits!";
        	}
        	/* The client tried to bet 0 credits. */
        	else if (credits == 0){
        		answer = "Are you kidding?! You have bet no credits!";
        	}
        	else{
	        	synchronized(betScheduler.getManager()){
		        	if((resultBet.equals("1") || resultBet.compareToIgnoreCase("x")==0 || resultBet.equals("2"))
		        			&& betScheduler.isValidGame(gameNumber)){
		        		/* Takes the credits bet from the client's account. */
		        		clientInfo.setCredits(clientInfo.getCredits() - credits);
		        		/* Creates the new bet and saves the new database into file. */
		        		betScheduler.addBet(new Bet(clientInfo.getUsername(),gameNumber,resultBet,credits));
		        		database.saveObjectToFile("clientsDatabase.bin", database.getClientsDatabase());
		
		        		answer = "Bet done!";
		        	}
		        	else {
		        		answer = "Invalid command or the game number that you entered isn't available.";
		        	}
	        	}
        	}
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